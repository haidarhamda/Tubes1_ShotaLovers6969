package Services;
import Enums.*;
import Models.*;


import java.util.*;
import java.util.stream.*;

import static java.lang.Math.*;
import static java.lang.Math.pow;

public class Fetch{

    private static GameObject bot;
    private static GameState gameState;
    private static GameObject newTarget;

    public static PlayerActions playerAction;
    public static int heading;
    public int getHeading(){return heading;}

    public PlayerActions getPlayerAction() {
        return playerAction;
    }

    public static void setAction(GameObject newBot, GameState newGameState){
        bot=newBot;
        gameState=newGameState;
        playerAction=PlayerActions.FORWARD;
        heading=decideNewHeading();
    }

//    public static int selectTarget(){
//        if (!gameState.getGameObjects().isEmpty()) {
//            var objectInside = getGameObjectInside();
//            var objectInFront = getGameObjectInsideFront();
//            var nearestFood = nearestObject(ObjectTypes.FOOD);
//
//            var target = nearestFood;
//            var distanceToNearestFood = getDistanceBetween(bot, nearestObject(ObjectTypes.FOOD));
//            var distanceToNearestGas = getDistanceBetween(bot, nearestObject(ObjectTypes.GAS_CLOUD));
//            var distanceToAsteroidField = getDistanceBetween(bot, nearestObject(ObjectTypes.ASTEROID_FIELD));
//            if (cekInside(gameState.getGameObjects(), ObjectTypes.GAS_CLOUD)) {
//                var nearestGasCloud = nearestObject(ObjectTypes.GAS_CLOUD);
//                if (checkCollision(bot, nearestFood, nearestGasCloud)) {
//                    var foodNotCollidingGas = NotCollidingObjects(ObjectTypes.FOOD, nearestGasCloud);
//                    if (foodNotCollidingGas != null) {
//                        nearestFood = foodNotCollidingGas.get(0);
//                        if (cekInside(gameState.getGameObjects(), ObjectTypes.ASTEROID_FIELD)) {
//                            var nearestAsteroid = nearestObject(ObjectTypes.ASTEROID_FIELD);
//                            if (checkCollision(bot, nearestFood, nearestAsteroid)) {
//                                var foodNotCollidingAsteroid = NotCollidingObjects(ObjectTypes.FOOD, nearestGasCloud);
//                                if (foodNotCollidingAsteroid != null) {
//                                    nearestFood = foodNotCollidingAsteroid.get(0);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (cekInside(gameState.getGameObjects(), ObjectTypes.SUPERFOOD)) {
//                var nearestSuperFood = nearestObject(ObjectTypes.SUPERFOOD);
//                if (cekInside(gameState.getGameObjects(), ObjectTypes.GAS_CLOUD)) {
//                    var nearestGasCloud = nearestObject(ObjectTypes.GAS_CLOUD);
//                    if (checkCollision(bot, nearestSuperFood, nearestGasCloud)) {
//                        var foodNotCollidingGas = NotCollidingObjects(ObjectTypes.SUPERFOOD, nearestGasCloud);
//                        if (foodNotCollidingGas != null) {
//                            nearestSuperFood = foodNotCollidingGas.get(0);
//                            if (cekInside(gameState.getGameObjects(), ObjectTypes.ASTEROID_FIELD)) {
//                                var nearestAsteroid = nearestObject(ObjectTypes.ASTEROID_FIELD);
//                                if (checkCollision(bot, nearestSuperFood, nearestAsteroid)) {
//                                    var foodNotCollidingAsteroid = NotCollidingObjects(ObjectTypes.SUPERFOOD, nearestGasCloud);
//                                    if (foodNotCollidingAsteroid != null) {
//                                        nearestSuperFood = foodNotCollidingAsteroid.get(0);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                if (getDistanceBetween(bot, nearestSuperFood) * 0.7 < getDistanceBetween(bot, nearestFood)) {
//                    target = nearestSuperFood;
//                }
//            }
//            return getHeadingBetween(target);
//        }
//        else return 0;
//    }
    private static GameObject getBestWeight(List<GameObject> gameObjectList){
        var p=gameObjectList.get(0);
        for (int i=0;i<gameObjectList.size();i++){
            if (i==1){
                if (getDistanceBetween(bot,p)>getDistanceBetween(bot,gameObjectList.get(i))*1.5){
                    p=gameObjectList.get(i);
                }
            } else if (i==2){
                if (getDistanceBetween(bot,p)>getDistanceBetween(bot,gameObjectList.get(i))*2){
                    p=gameObjectList.get(i);
                }
            }
        }
        return p;
    }
    public static int decideNewHeading(){
        var bestFood=selectTarget(ObjectTypes.FOOD);
        var bestSuperFood=selectTarget(ObjectTypes.SUPERFOOD);
        if (bestSuperFood!=null) {
            if (getDistanceBetween(bot, bestSuperFood) * 0.7 <= getDistanceBetween(bot, bestFood)) {
                return getHeadingBetween(bestSuperFood);
            } else {
                return getHeadingBetween(bestFood);
            }
        } else {
            return getHeadingBetween(bestFood);
        }
    }
    public static GameObject selectTarget(ObjectTypes objectTypes){
        if (!gameState.getGameObjects().isEmpty()) {
            if (cekInside(gameState.getGameObjects(), objectTypes)) {
                var nearestFood = Algorithm.getObjectsByDistance(bot,ObjectTypes.FOOD,gameState,false).get(0);
                var nearestSuperFood = nearestObject(ObjectTypes.SUPERFOOD);
                var target = nearestFood;
                if (cekInside(gameState.getGameObjects(), ObjectTypes.GAS_CLOUD)) {
                    var nearestFoodNotCollidingGas = NotCollidingObjects(objectTypes, Algorithm.getObjectsByDistance(bot,ObjectTypes.GAS_CLOUD,gameState,false));
                    if (cekInside(gameState.getGameObjects(), ObjectTypes.ASTEROID_FIELD)) {
                        var nearestFoodNotCollidingAsteroid = NotCollidingObjects(objectTypes, Algorithm.getObjectsByDistance(bot,ObjectTypes.ASTEROID_FIELD,gameState,false));
                        if (nearestFoodNotCollidingAsteroid != null) {
                            if (nearestFoodNotCollidingGas != null) {
                                List<GameObject> l1 = new ArrayList<GameObject>() {{
                                    add(nearestFood);
                                    add(nearestFoodNotCollidingAsteroid.get(0));
                                    add(nearestFoodNotCollidingGas.get(0));
                                }};
                                return getBestWeight(l1);
                            } else {
                                if (Algorithm.checkCollision(bot, nearestFood, Algorithm.getObjectsByDistance(bot,ObjectTypes.ASTEROID_FIELD,gameState,false))) {
                                    if (getDistanceBetween(bot, nearestFood) * 1.5 < getDistanceBetween(bot, nearestFoodNotCollidingAsteroid.get(0))) {
                                        return nearestFoodNotCollidingAsteroid.get(0);
                                    } else {
                                        return nearestFood;
                                    }
                                } else {
                                    return nearestFood;
                                }
                            }
                        } else {
                            if (nearestFoodNotCollidingGas != null) {
                                if (Algorithm.checkCollision(bot, nearestFood, Algorithm.getObjectsByDistance(bot,ObjectTypes.GAS_CLOUD,gameState,false))) {
                                    if (getDistanceBetween(bot, nearestFood) * 2 < getDistanceBetween(bot, nearestFoodNotCollidingGas.get(0))) {
                                        return nearestFood;
                                    } else {
                                        return nearestFoodNotCollidingGas.get(0);
                                    }
                                }
                                else {return nearestFood;}
                            } else {
                                return nearestFood;
                            }
                        }
                    } else {
                        if (nearestFoodNotCollidingGas != null) {
                            if (Algorithm.checkCollision(bot, nearestFood, Algorithm.getObjectsByDistance(bot,ObjectTypes.GAS_CLOUD,gameState,false))) {//gas
                                if (getDistanceBetween(bot, nearestFood) * 2 < getDistanceBetween(bot, nearestFoodNotCollidingGas.get(0))) {
                                    return nearestFood;
                                } else {
                                    return nearestFoodNotCollidingGas.get(0);
                                }
                            }
                            else {return nearestFood;}
                        } else {
                            return nearestFood;
                        }
                    }
                } else if (cekInside(gameState.getGameObjects(), ObjectTypes.ASTEROID_FIELD)) {
                    var nearestFoodNotCollidingAsteroid = NotCollidingObjects(objectTypes, Algorithm.getObjectsByDistance(bot,ObjectTypes.ASTEROID_FIELD,gameState,false));//asteroid
                    if (nearestFoodNotCollidingAsteroid != null) {
                        if (Algorithm.checkCollision(bot, nearestFood, Algorithm.getObjectsByDistance(bot,ObjectTypes.ASTEROID_FIELD,gameState,false))) {
                            if (getDistanceBetween(bot, nearestFood) * 1.5 < getDistanceBetween(bot, nearestFoodNotCollidingAsteroid.get(0))) {
                                return nearestFoodNotCollidingAsteroid.get(0);
                            } else {
                                return nearestFood;
                            }
                        } else {
                            return nearestFood;
                        }
                    } else {
                        return nearestFood;
                    }
                } else {
                    return nearestFood;
                }
            }
            else {return null;}
        }
        else {return null;}
    }
//    private static boolean checkCollision(GameObject bot,GameObject makan, GameObject debuff){
//        var x1=bot.getPosition().getX();
//        var y1=bot.getPosition().getX();
//        var x2=makan.getPosition().getX();
//        var y2=makan.getPosition().getY();
//        var cx=debuff.getPosition().getX();
//        var cy=debuff.getPosition().getY();
//        var radius=debuff.getSize();
//        if (x2 - x1 != 0) {
//            var m = (y2 - y1) / (x2 - x1);
//            var c = (((y2 - y1) * x1) / (x2 - x1)) + y1;
//            var descriminant = pow((2 * m * c), 2) - 4 * (1 + pow(m, 2)) * (pow(c, 2) - pow(radius, 2));
//            if (descriminant > 0) {
//                return false;
//            } else {
//                return true;
//            }
//        } else {return false;}
//    }
    public static List<GameObject> NotCollidingObjects(ObjectTypes objectTypes, List<GameObject> debuff){
        if (!gameState.getGameObjects().isEmpty()){
            var list = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == objectTypes &&
                            !Algorithm.checkCollision(bot,item,debuff)
                            && !cekBound(getHeadingBetween(item)))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            if (!list.isEmpty()){return list;} else {return null;}
        } else {return null;}
    }
    private static GameObject nearestObject(ObjectTypes objectTypes){
        if (!gameState.getGameObjects().isEmpty()){
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == objectTypes)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            if (!foodList.isEmpty()){return foodList.get(0);}else {return null;}
        } else {return null;}
    }
    private static List<GameObject> getGameObjectInside(){
        List<GameObject> objects=gameState.gameObjects.stream()
                .filter(item->getDistanceBetween(bot,item)<bot.getRadius()).collect(Collectors.toList());
        return objects;
    }
    public static boolean cekInside(List<GameObject> gameObjectList,ObjectTypes objectTypes){
        for (GameObject object:gameObjectList) {
            if (object.getGameObjectType()==objectTypes){
                return true;
            }
        }
        return false;
    }
    private static List<GameObject> getGameObjectInsideFront(){
        List<GameObject> objects=gameState.gameObjects.stream()
                .filter(item->cekFront(item)&&getDistanceBetween(bot,item)<bot.radius/2).collect(Collectors.toList());
        return objects;
    }
    private static double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private static int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (int) ((direction + 360) % 360);
    }
    private static boolean cekFront(GameObject gameObject){
        return getHeadingBetween(gameObject)<30 &&getHeadingBetween(gameObject)>-30;
    }
    public static double getDistancetoBound(int heading){
        double boundX=gameState.getWorld().getRadius()*cos(heading);
        double boundY=gameState.getWorld().getRadius()*sin(heading);
        double distance=sqrt(pow(boundX-bot.getPosition().getX(),2)+pow(boundY-bot.getPosition().getY(),2));
        return distance;
    }
    public static boolean cekBound(int heading){
        return getDistancetoBound(heading)<bot.getSize()+50;
    }
}

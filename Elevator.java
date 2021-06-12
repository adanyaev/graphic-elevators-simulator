package com.company;


import java.util.ArrayList;
import java.util.Random;

public class Elevator implements Runnable {

    elevator_status current_status = elevator_status.WAITING;
    ArrayList<Request> requestsWaiting = new ArrayList<Request>();
    ArrayList<Request> requestsRunning = new ArrayList<Request>();
    int current_floor = 0;

    public void newRequest(Request r){
        Random random = new Random();
        if (r.dir == direction.UP){
            r.target = random.nextInt(Main.num_floors-r.start-1)+r.start+1;
        } else if (r.dir == direction.DOWN){
            r.target = random.nextInt(r.start);
        }
        if (current_status == elevator_status.WAITING){
            if (r.start == current_floor){
                current_status = r.dir == direction.UP ? elevator_status.MOVING_UP : elevator_status.MOVING_DOWN;
                requestsRunning.add(r);
            } else {
                current_status = r.start > current_floor ? elevator_status.MOVING_UP : elevator_status.MOVING_DOWN;
                requestsWaiting.add(r);
            }
        } else {
            requestsWaiting.add(r);
        }
    }

    public void run(){
        try {
            while (true) {
                if (current_status == elevator_status.WAITING) {
                    Thread.sleep(50);
                    continue;
                }
                else {
                    Thread.sleep(Main.seconds_between_floors*1000);
                    current_floor += current_status == elevator_status.MOVING_UP ? 1 : -1;
                    ArrayList<Request> toBeDeleted = new ArrayList<Request>();
                    for (int i = 0; i < requestsRunning.size(); i++){
                        Request r = requestsRunning.get(i);
                        if (r.target == current_floor){
                            toBeDeleted.add(requestsRunning.get(i));
                        }
                    }
                    for (int i = 0; i < toBeDeleted.size(); i++){
                        requestsRunning.remove(toBeDeleted.get(i));
                    }
                    if (requestsWaiting.size() == 0 && requestsRunning.size() == 0){
                        current_status = elevator_status.WAITING;
                        continue;
                    }
                    if (requestsRunning.size() == 0){
                        Request closest_r = null;
                        int min_dist = Main.num_floors + 1;
                        for (int i = 0; i < requestsWaiting.size(); i++){
                            Request r = requestsWaiting.get(i);
                            int dist = Math.abs(r.start-current_floor);
                            if (dist < min_dist){
                                closest_r = r;
                                min_dist = dist;
                            }
                        }
                        if (closest_r != null){
                            if (closest_r.start == current_floor){
                                current_status = closest_r.dir == direction.UP ? elevator_status.MOVING_UP : elevator_status.MOVING_DOWN;
                                requestsRunning.add(closest_r);
                                requestsWaiting.remove(closest_r);
                            } else {
                                current_status = closest_r.start > current_floor ? elevator_status.MOVING_UP : elevator_status.MOVING_DOWN;
                            }
                        } else { continue;}
                    }
                    toBeDeleted = new ArrayList<Request>();
                    for (int i = 0; i < requestsWaiting.size(); i++){
                        Request r = requestsWaiting.get(i);
                        if (r.start == current_floor && requestsRunning.size() < Main.max_persons){
                            if ((r.dir == direction.UP && current_status == elevator_status.MOVING_UP) ||
                                    (r.dir == direction.DOWN && current_status == elevator_status.MOVING_DOWN)){
                                toBeDeleted.add(r);
                                requestsRunning.add(r);
                            }
                        }
                    }
                    for (int i = 0; i < toBeDeleted.size(); i++){
                        requestsWaiting.remove(toBeDeleted.get(i));
                    }
                }
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

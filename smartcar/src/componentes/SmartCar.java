package componentes;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class SmartCar {

    private static final int CONGESTION_THRESHOLD = 5; // Número de vehículos para considerar congestión
    protected String brokerURL = null;
    protected String smartCarID = null;
    protected RoadPlace rp = null; // Simula la ubicación actual del vehículo
    protected static SmartCar_InicidentNotifier notifier = null;

    // Contador estático para el número de vehículos en el tramo de carretera
    private static int vehicleCount = 0;

    private int currentSpeed = 60; // Velocidad inicial del vehículo

    public SmartCar(String id, String brokerURL) {
        this.setSmartCarID(id);
        this.brokerURL = brokerURL;

        // Inicializamos la ubicación del vehículo
        this.setCurrentRoadPlace(new RoadPlace("R5s1", 0));

        // Inicializar el notificador de incidentes
        notifier = new SmartCar_InicidentNotifier("congestion-notifier", this, this.brokerURL);
        notifier.connect();
    }

    public void setSmartCarID(String smartCarID) {
        this.smartCarID = smartCarID;
    }

    public String getSmartCarID() {
        return smartCarID;
    }

    public void setCurrentRoadPlace(RoadPlace rp) {
        this.rp = rp;
    }

    public RoadPlace getCurrentPlace() {
        return rp;
    }

    public void changeKm(int km) {
        this.getCurrentPlace().setKm(km);
    }

    public void getIntoRoad(String road, int km) {
        this.getCurrentPlace().setRoad(road);
        this.getCurrentPlace().setKm(km);

        // Incrementar el contador de vehículos
        synchronized (SmartCar.class) {
            vehicleCount++;
        }
        System.out.println(smartCarID + " entered road segment: " + road);
        System.out.println("Current vehicle count in segment: " + vehicleCount);
    }

    public void notifyIncident(String incidentType) throws Exception {
        if (SmartCar.notifier == null)
            return;

        SmartCar.notifier.checkTraffic(this.getSmartCarID(), incidentType, this.getCurrentPlace());
    }

    public void getOutOfRoad(String segment) {
        // Decrementar el contador de vehículos
        synchronized (SmartCar.class) {
            if (vehicleCount > 0) {
                vehicleCount--;
            }
        }
        System.out.println("Vehicle " + smartCarID + " left road segment: " + segment);
        System.out.println("Current vehicle count in segment: " + vehicleCount);
    }

        //1. Método para comprobar la congestión en el tramo de carretera
    public static void checkCongestion() throws Exception {
        if (vehicleCount >= CONGESTION_THRESHOLD) {
            System.out.println("Road is congested with " + vehicleCount + " vehicles.");

            //2. Publicar alerta de congestión
            JSONObject congestionAlert = new JSONObject();
            try {
                congestionAlert.put("event", "congestion");
                congestionAlert.put("road", notifier.getSmartCar().getCurrentPlace().getRoad()); // Obtener el tramo dinámicamente
                congestionAlert.put("vehicleCount", vehicleCount);

                // Construir el topic dinámicamente
                String road = notifier.getSmartCar().getCurrentPlace().getRoad(); // Obtener el nombre del tramo
                String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/info";

                MqttMessage message = new MqttMessage(congestionAlert.toString().getBytes());
                message.setQos(1); // Nivel de calidad del servicio (QoS)
                message.setRetained(false);

                // Usar el notifier para publicar
                if (notifier != null) {
                    notifier.publish(topic, message);
                    System.out.println("Congestion alert published to topic: " + topic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } 
    }

    //4. Método para actualizar la velocidad del vehículo
    public void updateSpeedLimit(String road, int speedLimit) throws Exception {
        // Construir el topic dinámicamente
        String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/info";

        if (this.rp.getRoad().equals(road)) {
            int newSpeed = Math.min(currentSpeed, speedLimit);
            JSONObject speedUpdateAlert = new JSONObject();
            try {
                speedUpdateAlert.put("event", "speedUpdate");
                speedUpdateAlert.put("vehicle", getSmartCarID());
                speedUpdateAlert.put("oldSpeed", currentSpeed);
                speedUpdateAlert.put("newSpeed", newSpeed);

                MqttMessage message = new MqttMessage(speedUpdateAlert.toString().getBytes());
                message.setQos(1); // Nivel de calidad del servicio (QoS)
                message.setRetained(false);

                // Usar el notifier para publicar
                if (notifier != null) {
                    notifier.publish(topic, message);
                    System.out.println("Speed update alert published to topic: " + topic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (newSpeed < currentSpeed) {
                System.out.println(smartCarID + " reduces speed to " + newSpeed + " km/h due to traffic signal on road " + road);
            } else if (newSpeed > currentSpeed) {
                System.out.println(smartCarID + " increases speed to " + newSpeed + " km/h due to traffic signal on road " + road);
            } else {
                System.out.println(smartCarID + " maintains speed at " + currentSpeed + " km/h on road " + road);
            }
            currentSpeed = newSpeed;
        }
    }

    public SmartCar_InicidentNotifier getNotifier() {
        return notifier;
    }

}

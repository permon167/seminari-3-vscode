package componentes;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class SmartCar_InicidentNotifier extends MyMqttClient {
	
	public SmartCar_InicidentNotifier(String clientId, SmartCar smartcar, String brokerURL) {
		super(clientId, smartcar, brokerURL);
	}

	public SmartCar getSmartCar() {
        return this.smartcar;
    }
	
	
	//1. Metodo para informar sobre el trafico en un tramo de carretera
	public void checkTraffic(String smartCarID, String notificationType, RoadPlace place) throws Exception {
	    String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + place.getRoad() + "/traffic";

	    JSONObject pubMsg = new JSONObject();
	    try {
	        pubMsg.put("vehicle", smartCarID);
	        pubMsg.put("event", notificationType);
	        pubMsg.put("road", place.getRoad());
	        pubMsg.put("kp", place.getKm());
	    } catch (JSONException e) {
	        e.printStackTrace();
	        return;
	    }

	    MqttMessage message = new MqttMessage(pubMsg.toString().getBytes());
	    message.setQos(0);
	    message.setRetained(false);

	    publish(topic, message);
	}

	 // Nuevo método para señales de tráfico (antes en TrafficSignal)
	 public void updateSpeedLimit(String road, int newSpeedLimit) throws Exception {
        String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/signals";
        JSONObject pubMsg = new JSONObject();
        try {
            pubMsg.put("type", "traffic-signal");
            pubMsg.put("msg", new JSONObject()
                    .put("road", road)
                    .put("speedLimit", newSpeedLimit));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
		
	    MqttMessage message = new MqttMessage(pubMsg.toString().getBytes());
	    message.setQos(1); // QoS nivel 1 para asegurar la entrega
	    message.setRetained(false);
		
        publish(topic, message);
        System.out.println("Updated speed limit to " + newSpeedLimit + " km/h on road " + road);
    }

	 //3. Metodo para actualizar el límite de velocidad con la señal de tráfico
	 public void signalSpeedLimit(String road, int newSpeedLimit) throws Exception {
        String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/signals";
		JSONObject pubMsg = new JSONObject();
		try{
        // Crear un mensaje JSON con el nuevo límite de velocidad
        
        pubMsg.put("type", "traffic-signal");
        pubMsg.put("msg", new JSONObject()
                .put("road", road)
                .put("speedLimit", newSpeedLimit));
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

        //3. Publicar el mensaje en el tópico de alertas de la señal de tráfico
		MqttMessage message = new MqttMessage(pubMsg.toString().getBytes());
	    message.setQos(1); // QoS nivel 1 para asegurar la entrega
	    message.setRetained(false);
		
        publish(topic, message);
        System.out.println("Traffic signal updated speed limit to " + newSpeedLimit + " km/h on road " + road);
    }



	//5. Método para reportar un accidente en un tramo
	public void reportAccident(String smartCarID, String road, int km) throws Exception {
	    String topic = "es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/alerts";

	    JSONObject pubMsg = new JSONObject();
	    try {
	        pubMsg.put("event", "Accident");
			pubMsg.put("kp", km);
	        pubMsg.put("vehicle", smartCarID);
	        pubMsg.put("road", road);
	    } catch (JSONException e) {
	        e.printStackTrace();
	        return;
	    }

	    MqttMessage message = new MqttMessage(pubMsg.toString().getBytes());
	    message.setQos(1); // QoS nivel 1 para asegurar la entrega
	    message.setRetained(false);

	    publish(topic, message);
	}
	
}
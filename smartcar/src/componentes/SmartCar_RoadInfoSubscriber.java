package componentes;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class SmartCar_RoadInfoSubscriber extends MyMqttClient {

    protected SmartCar theSmartCar;

    public SmartCar_RoadInfoSubscriber(String clientId, SmartCar smartcar, String MQTTBrokerURL) {
        super(clientId, smartcar, MQTTBrokerURL);
        this.theSmartCar = smartcar;
    }

  	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		super.messageArrived(topic, message);			// esto muestra el mensaje por pantalla ... comentar para no verlo
		String payload = new String(message.getPayload());

	// 	 // Procesar mensaje de estado de carretera
	// 	 if (topic.contains("/info")) {
	// 		System.out.println(theSmartCar.getSmartCarID() + " received road status update: " + payload);
	// 	}
		
	// 	// Procesar mensaje de entrada/salida de vehículos
	// 	if (topic.contains("/traffic")) {
	// 		System.out.println(theSmartCar.getSmartCarID() + " received traffic update: " + payload);
	// 	}

	// 	try {
    //     JSONObject json = new JSONObject(payload);
        
    //     // Procesar señal de tráfico
    //     if (topic.contains("/signals") && json.has("msg")) {
    //         JSONObject msg = json.getJSONObject("msg");
    //         if (msg.has("speedLimit")) {
    //             int newSpeedLimit = msg.getInt("speedLimit");
    //             theSmartCar.updateSpeedLimit(theSmartCar.getCurrentPlace().getRoad(), newSpeedLimit);
    //         }
    //     }
	// 	} catch (JSONException e) {
	// 		e.printStackTrace();
	// 	}

	// 	try {
	// 		JSONObject json = new JSONObject(payload);
			
	// 		// Procesar alertas de accidente
	// 		if (topic.contains("/info") && json.has("event") && 
	// 			json.getString("event").equals("Accident")) {
	// 			System.out.println(theSmartCar.getSmartCarID() + " received accident alert at road " + 
	// 				json.getString("road") + ", km " + json.getInt("kp"));
	// 		}
	// 	} catch (JSONException e) {
	// 		e.printStackTrace();
	// 	}
	 }
    
}

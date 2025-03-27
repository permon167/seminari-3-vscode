import componentes.SmartCar;
import componentes.SmartCar_InicidentNotifier;
import componentes.SmartCar_RoadInfoSubscriber;

public class SmartCarStarterApp {
    public static void main(String[] args) throws Exception {
       

        if (args.length < 2) {
            System.out.println("Usage: SmartCarStarterApp <smartCarID> <brokerURL>");
            System.exit(1);
        }

        String smartCarID = args[0]; // Primer argumento: ID del vehículo
        String brokerURL = args[1];  // Segundo argumento: URL del broker MQTT

        //2. Número de vehículos a inicializar
        int numberOfVehicles = 6; // Cambia este valor según el umbral de congestión
        String road = "R5s1";
        int km = 0;

        // Crear e inicializar múltiples vehículos
        SmartCar[] vehicles = new SmartCar[numberOfVehicles];
        for (int i = 0; i < numberOfVehicles; i++) {
            String vehicleID = smartCarID + "-" + (i + 1); // Generar un ID único para cada vehículo
            vehicles[i] = new SmartCar(vehicleID, brokerURL);


        // // Suscribirse a los topics necesarios
        // SmartCar_RoadInfoSubscriber subscriber = new SmartCar_RoadInfoSubscriber(
        //     "subscriber-" + vehicleID, vehicles[i], brokerURL);
        // subscriber.connect();
        // subscriber.subscribe("es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/info");
        // subscriber.subscribe("es/upv/pros/tatami/smartcities/traffic/PTPaterna/road/" + road + "/signals");
           

            // Simular que el vehículo entra en el tramo
            vehicles[i].getIntoRoad(road, km);
            vehicles[i].notifyIncident("Entering road segment");
          

            //2. Comprobar el estado de congestión después de cada entrada
            SmartCar.checkCongestion();

            

            // Simular un pequeño retraso entre entradas
            Thread.sleep(1000);
        }
  
         // Obtener el notificador del vehículo
         SmartCar_InicidentNotifier notifier = new SmartCar_InicidentNotifier(road, null, brokerURL); // Necesitarás añadir este getter en SmartCar
        notifier.connect();
         // Actualizar límite de velocidad (antes con TrafficSignal)
         notifier.signalSpeedLimit(road, 50);

        //4. Iterar sobre todos los vehículos para actualizar su velocidad
        for (SmartCar vehicle : vehicles) {
            vehicle.updateSpeedLimit(road, 50);
        }

        //5. El primer vehículo reporta un accidente
        notifier.reportAccident(vehicles[0].getSmartCarID(), road,km);

    }
}

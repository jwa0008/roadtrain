package Vehicle;

import App.RoadTrainApp;



public class Car implements Vehicle {

	int id, port;
	RoadTrainApp app;
	
	public Car(int id, int port){
		super();
		this.id = id;
		this.port = port;
	}

	@Override
	public void startApp() {
		// TODO Auto-generated method stub
		app = new RoadTrainApp(false, id, port);
	}

	@Override
	public void joinTrain() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leaveTrain() {
		// TODO Auto-generated method stub
		
	}
	
	
	

}

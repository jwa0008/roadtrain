package Vehicle;

import App.RoadTrainApp;

public class Truck implements Vehicle {
	
	private int id, port;
	RoadTrainApp app;

	public Truck(int id, int port){
		this.id = id;
		this.port = port;
		startApp();
	}
	
	@Override
	public void startApp() {
		// TODO Auto-generated method stub
		app = new RoadTrainApp(true, id, port);
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

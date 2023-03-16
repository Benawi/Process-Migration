package framework;

public class ThreadPlus extends Thread{

	private MigratableProcess mp;
	
	public ThreadPlus(Runnable r){
		super(r);
		this.mp = (MigratableProcess) r;
	}
	
	public MigratableProcess getMP(){
		return mp;
	}
	
	@Override
	public void run(){
		super.run();
		
	}
}

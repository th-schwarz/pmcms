package de.thischwa.pmcms.launcher;


public class MyClassToLaunch {

	public static void main(String[] args) {
		if(args.length > 0) {
			System.out.print("args: ");
			for(int i=0; i<args.length; i++)
				System.out.print(args[i]+", ");
			System.out.println();
		}
		for(int i=0; i<10; i++) {
			System.out.print('.');
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

}

package com.zafar.control;

import java.io.IOException;
import java.util.Properties;


public class CommandMaster {

	private Dictionary dictionary=new Dictionary();
	private ControlScreen controller=new ControlScreen();
	private Properties gestureMapper=new Properties();
	public CommandMaster(){
		try {
			gestureMapper.load(CommandMaster.class.getClassLoader().getResourceAsStream("gestureMapper.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	public String sanitize(String string)
	{
		return string.replace(' ', '_');
	}
	public Command interpret(String command) throws CommandNotFoundException
	{
		Command[] commands=null;

		commands=dictionary.getCommands(sanitize(command));
		if(System.getProperty("os.name").toLowerCase().indexOf("win")>=0)
		{
			for(int i=0;i<commands.length;i++)
				if(commands[i].getPlatform().toString().equals("win"))				
					return commands[i];
		}
		else
			if(System.getProperty("os.name").toLowerCase().indexOf("nux")>=0)
			{
				for(int i=0;i<commands.length;i++)
					if(commands[i].getPlatform().toString().equals("nix"))				
						return commands[i];					
			}

		return null;

	}
	public void executeCommand(String gesture){
		String command=(String) gestureMapper.get(gesture);
		Runnable job=()->{
				try {
					controller.execute(interpret(command));
				} catch (CommandNotFoundException e) {
					e.printStackTrace();
				}
		};
		new Thread(job).start();
	}
}

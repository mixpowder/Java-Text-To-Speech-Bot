package mixpowder.jttsbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import mixpowder.jttsbot.jttsbot;

public class ShutdownCommand extends Command{

	private jttsbot main;

	public ShutdownCommand(jttsbot main){
		this.main = main;
		this.name = "shutdown";
		this.help = "bot停止コマンド";
	}

	@Override
	protected void execute(CommandEvent e) {
		e.getJDA().shutdown();
	}
}
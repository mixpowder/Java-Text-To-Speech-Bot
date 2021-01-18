package mixpowder.jttsbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import mixpowder.jttsbot.jttsbot;

public class StopCommand extends Command{

	private jttsbot main;

	public StopCommand(jttsbot main){
		this.main = main;
		this.name = "stop";
		this.help = "読み上げ停止コマンド";
	}

	@Override
	protected void execute(CommandEvent e) {
		if(this.main.start == true){
			this.main.audioManager().closeAudioConnection();
			this.main.player().destroy();
			this.main.start = false;
			e.reply("読み上げを終了します");
		}else{
			e.reply("読み上げが開始されていません");
		}
	}
}
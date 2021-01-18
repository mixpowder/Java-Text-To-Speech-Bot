package mixpowder.jttsbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import mixpowder.jttsbot.jttsbot;
import mixpowder.jttsbot.main.AudioPlayerSendHandler;

public class StartCommand extends Command{

	private jttsbot main;

	public StartCommand(jttsbot main){
		this.main = main;
		this.name = "start";
		this.help = "読み上げ開始コマンド";
	}

	@Override
	protected void execute(CommandEvent e) {
		if(!(e.getMember().getVoiceState().getChannel() == null) && this.main.start == false){
			e.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(this.main.player()));
			this.main.setAudioManager(e.getGuild().getAudioManager());
			this.main.audioManager().openAudioConnection(e.getMember().getVoiceState().getChannel());
			this.main.player().setVolume(10);
			this.main.start = true;
			this.main.textChannel(e.getTextChannel());
			e.reply("読み上げを開始します");
		}else{
			e.reply("すでに読み上げを開始しています");
		}
	}
}
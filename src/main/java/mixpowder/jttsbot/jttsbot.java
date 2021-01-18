package mixpowder.jttsbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.security.auth.login.LoginException;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import mixpowder.jttsbot.commands.ShutdownCommand;
import mixpowder.jttsbot.commands.StartCommand;
import mixpowder.jttsbot.commands.StopCommand;
import mixpowder.jttsbot.gui.ErrorFrame;
import mixpowder.jttsbot.main.TrackScheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;


public class jttsbot extends ListenerAdapter{
	private AudioPlayerManager playerManager;
	private AudioManager audioManager;
	private AudioPlayer player;
	private TrackScheduler scheduler;
	private static ObjectNode node;
	public Boolean start = false;
	private TextChannel channel;

	public static void main(String[] args) {
		 Bot();
	}

	public jttsbot(){
		jttsbot.node = (new JsonSettings()).Settings();
		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
		this.player = playerManager.createPlayer();
		this.scheduler = new TrackScheduler(player);
		this.player.addListener(scheduler);
	}

	@SuppressWarnings("deprecation")
	public static JDA Bot(){
		jttsbot main = new jttsbot();
		JDA jda = null;
		CommandClientBuilder cc = new CommandClientBuilder()
				.addCommands(new StartCommand(main),new StopCommand(main),new ShutdownCommand(main))
				.setPrefix(node.get("SetPrefix").textValue())
				.setOwnerId(node.get("OwnerID").textValue());
		try {
			jda = (new JDABuilder())
					.setToken(node.get("BotToken").textValue())
					.addEventListeners(main,cc.build())
					.build();
		} catch (LoginException e) {
			new ErrorFrame("BotTokenを正しく入力して下さい 内容: " + e.getMessage());
		}
		return jda;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		String[] s = (e.getMessage().getContentRaw()).split(" ");
		if(!e.getAuthor().isBot()) {
			if(start && e.getTextChannel() == this.channel){
				POST(String.join(" ",s));
			}
		}
	}

	public void textChannel(TextChannel channel){
		this.channel = channel;
	}

	public void loadAndPlay(String trackUrl) {
	    playerManager.loadItemOrdered(scheduler, trackUrl, new AudioLoadResultHandler() {
	    	@Override
	    	public void trackLoaded(AudioTrack track) {
	    		scheduler.queue(track);
	    	}

	    	public void playlistLoaded(AudioPlaylist playlist) {
	    	}

	    	public void noMatches() {
	      	}

	      	public void loadFailed(FriendlyException exception) {
	      	}
	    });
	}

	public void POST(String voice){
		String urlString = "http://open-jtalk.sp.nitech.ac.jp/index.php";
		try {
			String postStr = "SPKR=1&SYNALPHA=0.55&F0SHIFT=0&DURATION=1&SYNTEXT=" + URLEncoder.encode(voice, "euc-jp") + "&FLAG=1";
            URL url = new URL(urlString);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            OutputStream os = uc.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(postStr);
            ps.close();

            InputStream is = uc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String s = "";
            while ((s = reader.readLine()) != null)if(s.contains("./temp"))break;

            reader.close();
            int a,b;
            a = s.indexOf("<a href=")+10;
            b = s.substring(a).indexOf(">")-1;
            loadAndPlay("http://open-jtalk.sp.nitech.ac.jp" + s.substring(a,a + b));
        } catch (IOException e) {
        }
	}

	public AudioManager audioManager(){
		return audioManager;
	}

	public TrackScheduler scheduler(){
		return scheduler;
	}

	public AudioPlayer player(){
		return player;
	}

	public void setAudioManager(AudioManager manager){
		audioManager = manager;
	}
}

class JsonSettings{

	ObjectNode Settings(){
		File file = new File("Settings.json");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = null;
		try {
			if(!file.exists()){
				file.createNewFile();
				node = mapper.readTree(mapper.writeValueAsString(new JsonSettings())).deepCopy();
				mapper.writer(new DefaultPrettyPrinter()).writeValue(file, node);
			}else{
				node = mapper.readTree(file).deepCopy();
			}
		}catch (IOException e){

		}
		return node;



	}

	public String SetPrefix = "$";
	public String OwnerID = "ID";
	public String BotToken = "Token";
}

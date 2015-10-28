/*
 * Discord4J - Unofficial wrapper for Discord API
 * Copyright (c) 2015
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package sx.blah.discord;

import org.json.simple.parser.ParseException;
import sx.blah.discord.handle.IListener;
import sx.blah.discord.handle.impl.events.InviteReceivedEvent;
import sx.blah.discord.handle.impl.events.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Channel;
import sx.blah.discord.handle.obj.Invite;
import sx.blah.discord.handle.obj.Message;
import sx.blah.discord.handle.obj.PrivateChannel;
import sx.blah.discord.util.MessageBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author qt
 * @since 8:00 PM 16 Aug, 2015
 * Project: DiscordAPI
 * <p>
 * General testing bot. Also a demonstration of how to use the bot.
 */
public class CCCDiscordBot {

	/**
	 * Starts the bot. This can be done any place you want.
	 * The main method is for demonstration.
	 *
	 * @param args Command line arguments passed to the program.
	 */
	public static void main(String... args) {
		try {
			
			Random random = new Random();
			
			DiscordClient.get().login("cccpizzabot@gmail.com" /* username */, "BotPizzaCCC" /* password */);
			
			System.out.println("Logged in");

			DiscordClient.get().getDispatcher().registerListener(new IListener<MessageReceivedEvent>() {
				@Override public void receive(MessageReceivedEvent messageReceivedEvent) {
					Message m = messageReceivedEvent.getMessage();
					
					System.out.println("Got here");
					
					if (m.getContent().startsWith(".meme")
							|| m.getContent().startsWith(".nicememe")) {
							new MessageBuilder().appendContent("MEMES REQUESTED:", MessageBuilder.Styles.UNDERLINE_BOLD_ITALICS)
                                    .appendContent(" http://niceme.me/").withChannel(messageReceivedEvent.getMessage().getChannel())
                                    .build();
					} else if (m.getContent().startsWith(".clear")) {
						Channel c = DiscordClient.get().getChannelByID(m.getChannel().getID());
						if (null != c) {
							c.getMessages().stream().filter(message -> message.getAuthor().getID()
									.equalsIgnoreCase(DiscordClient.get().getOurUser().getID())).forEach(message -> {
								try {
									Discord4J.logger.debug("Attempting deletion of message {} by \"{}\" ({})", message.getID(), message.getAuthor().getName(), message.getContent());
									DiscordClient.get().deleteMessage(message.getID(), message.getChannel().getID());
								} catch (IOException e) {
									Discord4J.logger.error("Couldn't delete message {} ({}).", message.getID(), e.getMessage());
								}
							});
						}
					} else if (m.getContent().startsWith(".name ")) {
						String s = m.getContent().split(" ", 2)[1];
						try {
							DiscordClient.get().changeAccountInfo(s, "", "");
							m.reply("is this better?");
						} catch (ParseException | IOException e) {
							e.printStackTrace();
						}
					} else if(m.getContent().startsWith(".pm")) {
                        try {
                            PrivateChannel channel = DiscordClient.get().getOrCreatePMChannel(m.getAuthor());
                            new MessageBuilder().withChannel(channel).withContent("SUP DUDE").build();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
					}else if(m.getContent().startsWith("!ping")){
						try {
							m.reply("Pong");
						}catch (IOException | ParseException e) {
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!fhomepage")){
						try {
							m.reply("http://fuckinghomepage.com");
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
                    }else if(m.getContent().startsWith("!404")){
						try {
							Document doc = Jsoup.connect("http://thebest404pageever.com/swf/index.php") //It didnt like bots, so I needed to make it look like a firefox client
									.userAgent("Mozilla/5.0")
									.get();
							Elements links = doc.select("a[href]");
							int randomLinkNumber = random.nextInt(links.size()) + 1;
							int count = 0;
							String finalWebsiteLink = null;
								for (Element link : links){ //Generates an array of links in the website
									if (count == randomLinkNumber){
										finalWebsiteLink = link.attr("abs:href"); //grab the random link
									}
									count++;
						        }
							m.reply(finalWebsiteLink);
							
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!roll")){
						try {
							if (m.getContent().substring(6, m.getContent().length()).equals("0")){
								m.reply("please enter a valid number.");
							}else{
								String range = m.getContent().substring(6, m.getContent().length()); //Grab the range of the random generator
								try{
									int number = Integer.parseInt(range);
									int randomNumber = random.nextInt(number) + 1;
									m.reply(randomNumber + " is your random number.");
								}catch(NumberFormatException nfe){
									m.reply("please enter a valid number.");
								}catch (IOException | ParseException e){
									e.printStackTrace();
								}
							}
						}catch(IOException | ParseException | NumberFormatException nfe){
							try {
								m.reply("please enter a valid number.");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
							
					}else if(m.getContent().startsWith("!pokemon")){
						try {
							String pokemon = ""; //the line of text it is currently on.
							int randomLineNumber = random.nextInt(720) + 1;
							FileReader filereader = new FileReader("pokemon.txt"); //Text document with the list of all the pokemon
							BufferedReader bufferedreader = new BufferedReader(filereader);
							for(int x = 0; x != randomLineNumber; x++){
								pokemon = bufferedreader.readLine();
							}
							bufferedreader.close();
							m.reply("All right! " + pokemon + " was caught!");
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().trim().matches("!e621") && m.getChannel().getID().contains("90944841218801664")){
						try {
							Document doc = Jsoup.connect("http://www.e621.net/post")
									.userAgent("Mozilla/5.0")
									.get();
							Elements media = doc.select("[src]");
							int randomLinkNumber = random.nextInt(media.size()) + 1;
							int count = 0;
							String finalWebsiteLink = null;
								for (Element link : media){
									if (count == randomLinkNumber){
										finalWebsiteLink = link.attr("abs:src");
									}
									count++;
						        }
							m.reply(finalWebsiteLink);
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!time")){
						try{
							String message = m.getContent(); //grab the message
							try{
								String zone = message.substring(6, message.length()).trim(); //grab the time zone from !time command
								String actualZone = null;
								
								if(zone.equals("arizona")){ //translate user text to an actual zoneID!
									actualZone = "America/Phoenix";
								}else if(zone.equals("michigan")){
									actualZone = "America/Detroit";
								}else if(zone.equals("connecticut")){
									actualZone = "America/Detroit";
								}else if(zone.equals("california")){
									actualZone = "America/Los_Angeles";
								}else if(zone.equals("weeb")){
									actualZone = "Japan";
								}
								
								LocalDateTime ldt = LocalDateTime.now(ZoneId.of(actualZone)); //set the local time in terms of the zone
								ZonedDateTime tzDateTime = ldt.atZone(ZoneId.of(actualZone)); //set the zone for what is wanted
								DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm:ss a z"); // HOUR:MINUTE:SECONDS AM/PM TIMEZONE
								
								m.reply(tzDateTime.format(format));
							}catch(Exception e){
								m.reply("please enter a valid ZoneID");
							}
							
						}catch(IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!aboutme")){
						try {
							m.reply("Name: " + m.getAuthor().getName()
									+ " AuthorID: " + m.getAuthor().getID()
									+ " Avatar Image: " + m.getAuthor().getAvatarURL());
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!channelID")){
						try {
							m.reply(" ChannelID: " + m.getChannel().getID());
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!no more pizza") && m.getAuthor().getID().trim().equals("79429897923067904")){ //my ID
						try {
							m.reply("RIP IN PEPPERONIS");
							System.exit(0);
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!help")){
						try {
							m.reply("Help document located here: https://docs.google.com/document/d/1tWOmuQXzRlAppP1R6vabyUMtZ84_mmJtiwlMs_uXRmw/edit?usp=sharing");
						}catch (IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().contains("( ͡° ͜ʖ ͡°)")){
						try{
							m.reply("I cannot compute 'lenny face'");
						}catch(IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!getrekt")){
						try{
							DiscordClient.get().sendMessage("r   e   k   t", m.getChannel().getID());
							DiscordClient.get().sendMessage("e", m.getChannel().getID());
							DiscordClient.get().sendMessage("k", m.getChannel().getID());
							DiscordClient.get().sendMessage("t", m.getChannel().getID());
						}catch(IOException | ParseException e){
							e.printStackTrace();
						}
					}else if(m.getContent().startsWith("!supreme") && m.getAuthor().getID().equals("79353745091080192")){
						try{
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMM  MMM     MM     MM  MMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMM    MMMMMMMMMMMMMMM     MMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMM  MMMMMM        MMMMM  MMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMM    MMMM               MMMMM   MMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MM      MM      MM   MM      MM      MMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`M       MM      MM   MM      MMM      MM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`M       MM      MM   MM      MM       MM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMM    MMM    MMMMMMMMM    MMM    MMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMM    MMMMMMM    MMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMM  MMMM           MMMM   MMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMM   MMMMMM        MMMMM   MMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMM  M      MMMMMMMMM      M  MMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMM          MMMMMMM          MMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMMM  MMMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMM    MMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMM  MMMMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMM   MMMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMM    MMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMMMM   MMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMMMMM   MMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMM MM      M MMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMM M       M MMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMMMMMMMMM MMMMMMMMMMMMMMMM`", m.getChannel().getID());
							DiscordClient.get().sendMessage("`MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM`", m.getChannel().getID());
						}catch(IOException | ParseException e){
							e.printStackTrace();
						}
					}
					
					
				}
			});

			DiscordClient.get().getDispatcher().registerListener(new IListener<InviteReceivedEvent>() {
				@Override public void receive(InviteReceivedEvent event) {
					Invite invite = event.getInvite();
					try {
						Invite.InviteResponse response = invite.details();
						event.getMessage().reply(String.format("you've invited me to join #%s in the %s guild!", response.getChannelName(), response.getGuildName()));
                        invite.accept();
                        DiscordClient.get().sendMessage(String.format("Hello, #%s and the \\\"%s\\\" guild! I was invited by %s!",
                                        response.getChannelName(), response.getGuildName(), event.getMessage().getAuthor()),
								response.getChannelID());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			DiscordClient.get().getDispatcher().registerListener(new IListener<MessageDeleteEvent>() {
				@Override public void receive(MessageDeleteEvent event) {
					try {
						event.getMessage().reply("you said, \\\"" + event.getMessage().getContent() + "\\\"");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

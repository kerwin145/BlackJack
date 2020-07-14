
/*
 * NOTES: The updateComputerCard and updatePlayerCard methods are different. 

 * 		updatePlayerCard() also assigns a value and updates graphics. so does computerTurn()
 * 		New Round deals two player cards and one computer card.
 * 		NewGame and UpdateCard are very similar. New round, however, resets the round and deals 2 card. UpdateCard only deals card, and once. 
 * 		currentCard index starts out 1. However, for the card array, it starts at 0. 
 * 		The outcome methods includes the win lose and tie methods .The win lose and tie methods also control the split rounds. 
 * 
 * 		IMO, The most important methods are: NewGame() and Outcome(). The two most important variables are NewGame and CurrentCard
 * TODO: Computer Soft Hand!
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;


public class GUI implements ActionListener {
	
	private JFrame frame;
	private JLabel label1; //player output.
	private JLabel label2; //computer output
	private JLabel label3; //bet and current currency.
	private JPanel panel;
	
	private JTextField input;
	
	String labelText = ""; //displays player's current card and player total. Corresponds to label1
	String label2Text = ""; //displays computer's card and total. Corresponds to label2
	String cardText = ""; //takes the card name, and is used in labelText. 
	int number = 0; //This variable will be the same as number of the card. Also will be used in score calculations. 
	int currentCard = 1; //out of the five cards, this will control which card will be flipped. 
	int cCard = 1; //same thing as current card but for the computer
	int playerTotal = 0; 
	int cTotal = 0;
	
	int card1Val; //these will keep track of the values of the first two cards. Necessary for the split card and Ace soft-hand functions.
	int card2Val; 
	char card1Char;//takes the first letter of the card. This is because kings, queens, jacks have the same value, so deciding on allowing a split can not be based on value.  
	char card2Char;
	Image card2Image;
	ImageIcon card2Icon;
	
	Image card2ImageSmall;
	ImageIcon card2IconSmall; //version that can be stored in CardHold
	int splitRound = 1; //there are two rounds for a split. One for each card. Will be incremented in the win, lose, and tie methods. Wil; be evaluated in the draw section. 

	
	int bet = 0;
	int bbucks = 1000; 
	
	int theme = 0; //will be from 0 to 2. This will allow the user to control the theme of the cards. 
	
	boolean softHand = false; //true if player gets an ace. 
	boolean doSplit = false; //Split! Oh no...
	boolean newGame = true; //controls the reseting of cards, makes the "stand" button dead at the start of the game, and prevents player from changing wager mid game. 
	boolean splitWagerChange = true; //Appears in win, lose, tie, and split functions. 
	//newGame and splitWagerChange both have to be true for the player to be allowed to change his/her wager. 
	boolean insurance = false; //this is getting complex...
	boolean doubleDown = false; //used in the outcome method to immediately compare player and computers scores.
	
//Create Player Cards	
	JLabel Card1 = new JLabel("");
	JLabel Card2 = new JLabel("");
	JLabel Card3 = new JLabel("");
	JLabel Card4 = new JLabel("");
	JLabel Card5 = new JLabel("");
	JLabel Card6 = new JLabel("");
	JLabel CardHold = new JLabel(""); //this will be filled only when there is a card on hold during a split. 
	JLabel inputText = new JLabel("Bet: $");
	
	JLabel[] playerCards = {Card1, Card2, Card3, Card4, Card5, Card6};

//Create Computer Cards	
	JLabel cCard1 = new JLabel("");
	JLabel cCard2 = new JLabel("");
	JLabel cCard3 = new JLabel("");
	JLabel cCard4 = new JLabel("");
	JLabel cCard5 = new JLabel("");
	JLabel cCard6 = new JLabel("");
	
	JLabel[] computerCards = {cCard1, cCard2, cCard3, cCard4, cCard5, cCard6};
	
//Quality of Life buttons
	JButton changeTheme = new JButton("Change Theme");
	JButton tutorial = new JButton("Rules");
//Game Buttons	
	JButton Draw = new JButton("Start Round");
	JButton Stay = new JButton("Stand");
	JButton DoubleDown = new JButton("Double Down");
	

//this sets the background of the buttons
	ImageIcon img = new ImageIcon("blue_back.png");
	Image resized = img.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
	ImageIcon newImg = new ImageIcon(resized);
	
	Image cardHoldResized = img.getImage().getScaledInstance(90, 150, java.awt.Image.SCALE_AREA_AVERAGING);
	ImageIcon newCardHold = new ImageIcon(cardHoldResized);
	
	ImageIcon cImg = new ImageIcon("gray_back.png");
	Image cResized = cImg.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
	ImageIcon cNewImg = new ImageIcon(cResized);
	final ImageIcon cNewImg_BACKUP = cNewImg; //this will not be changed


	
//GOOOOOOOOIIII
	public GUI() throws IOException {
		
	//sets up the cards
		
		//player cards
		for (int i = 0; i < 6; i++)
			playerCards[i].setIcon(newImg);
		CardHold.setIcon(newCardHold);
		//computer cards
		for (int i = 0; i < 6; i++)
			computerCards[i].setIcon(cNewImg);
		
		
	//ACTION BUTTONS: DRAW STAY and DOUBLEDOWN
	
		Draw.setPreferredSize(new Dimension(10, 40));
		Draw.setBackground(Color.WHITE);
		Draw.setBorder(new LineBorder(Color.CYAN));
		Draw.setOpaque(true);
		Draw.setBorderPainted(true);
		
		Draw.setActionCommand("Draw");
		Draw.addActionListener(this);

		
		Stay.setPreferredSize(new Dimension(10, 40));
		Stay.setBackground(Color.WHITE);
		Stay.setBorder(new LineBorder(Color.BLUE));
		Stay.setOpaque(true);
		Stay.setBorderPainted(true);
		
		Stay.setActionCommand("Stay");
		Stay.addActionListener(this);
		
		
		DoubleDown.setPreferredSize(new Dimension(10, 40));
		DoubleDown.setBackground(Color.WHITE);
		DoubleDown.setBorder(new LineBorder(Color.RED));
		DoubleDown.setOpaque(true);
		DoubleDown.setBorderPainted(true);
		
		DoubleDown.setActionCommand("DoubleDown");
		DoubleDown.addActionListener(this);
	
	//INPUT: BETS
		input = new JTextField("0", 40);
		input.addActionListener(this);
		input.setActionCommand("Input");
		
	//FORMAT
		label1 = new JLabel("Player Output: Click a Card!");
		label2 = new JLabel("Computer Output: Waiting Your Turn!");
		label3 = new JLabel("Your starting BlackJack Bucks = $" + bbucks +". Please Type in a bet and press enter");
		
		changeTheme.addActionListener(this);
		changeTheme.setActionCommand("changeTheme");
		changeTheme.setPreferredSize(new Dimension(120, 30));
		
		tutorial.addActionListener(this);
		tutorial.setActionCommand("Tutorial");
		tutorial.setPreferredSize(new Dimension(120, 30));
				
		panel = new JPanel();
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //top, bottom, left, right
		
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();  
		panel.setLayout(layout);
		panel.setOpaque(false);
		panel.setBackground(Color.RED);
		
	//ADDS EVERY BUTTON AND LABEL
        gbc.weighty=0.5; 

		for (int i = 0; i < 6; i++) {
		    gbc.fill = GridBagConstraints.HORIZONTAL;  
			gbc.gridx = i;
			gbc.gridy = 0;
			panel.add(playerCards[i], gbc);
		}
		gbc.gridx = 6;
		gbc.gridy = 0;
		panel.add(CardHold, gbc);
		for (int i = 0; i < 6; i++) {
		    gbc.fill = GridBagConstraints.HORIZONTAL;  
			gbc.gridx = i;
			gbc.gridy = 1;
			panel.add(computerCards[i], gbc);
		}
		
	    gbc.fill = GridBagConstraints.HORIZONTAL;  
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(changeTheme, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		panel.add(tutorial, gbc);
		gbc.gridx = 2;
		gbc.gridy = 2;
		panel.add(inputText, gbc);
		inputText.setHorizontalAlignment(SwingConstants.RIGHT);
		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		panel.add(input, gbc);
		
	    gbc.fill = GridBagConstraints.HORIZONTAL;  
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 6;
		panel.add(label1, gbc);
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;  
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 6;
		panel.add(label2, gbc);
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		
	    gbc.fill = GridBagConstraints.HORIZONTAL;  
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 6;
		panel.add(label3, gbc);
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		Insets i = new Insets(2, 10, 10, 2); //top, left, bottom, right		 
	    gbc.fill = GridBagConstraints.HORIZONTAL; 
	    gbc.insets = i;
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		panel.add(Draw, gbc);
		gbc.gridx = 2;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		panel.add(Stay, gbc);
		gbc.gridx = 4;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		panel.add(DoubleDown, gbc);
	//fonts
		Font actionButtonFont = new Font("American Typewriter", Font.PLAIN, 20);
		Draw.setFont(actionButtonFont);
		Stay.setFont(actionButtonFont);
		DoubleDown.setFont(actionButtonFont);
		Font labelFont = new Font("Times New Roman", Font.PLAIN, 15);
		label1.setFont(labelFont);
		label2.setFont(labelFont);
		label3.setFont(labelFont);
		Font moreFont = new Font("Tahoma", Font.PLAIN, 12);
		changeTheme.setFont(moreFont);
		tutorial.setFont(moreFont);
		
		frame = new JFrame("BlackJack");
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("BLACK-JACK");
		frame.pack();
		frame.setSize(1230,800);
		frame.setVisible(true);

	}

	public void paint(Graphics g) {
		
		 Image background = Toolkit.getDefaultToolkit().createImage("RedBackground.png");
		 background.getScaledInstance(1230, 800, java.awt.Image.SCALE_AREA_AVERAGING);
		 g.drawImage(background, 0, 0, null);
	  }
	
	public static void main(String[] args) throws IOException {
		
		new GUI() {}; //creates new GUI object
		
	}

//EVENT LISTENER: THE DADDY
	@Override
	public void actionPerformed(ActionEvent e) {
		String cardCheck = e.getActionCommand();
		
//------------------------------------------------------------------------------------------------------------//

		if (cardCheck.equals("Split")) { 
			doSplit = true;
			splitWagerChange = false; //doesn't allow wager change mid split. 
			splitRound = 1;			
			
			playerTotal = card1Val; 
			currentCard = 2;
			
			updatePlayerCard(); //deals the second card. 
			outcome(); //check if player wins / loses (maybe a natural blackjack, who knows?)
			
			CardHold.setIcon(card2IconSmall);
			
			Draw.setText("Round 1 Draw!");
			Draw.setActionCommand("doSplit");
			Stay.setText("Stand");
			Stay.setActionCommand("Stay");
			
		
		}
			
		if (cardCheck.equals("noSplit")) { //things get set back to normal
			doSplit = false;
			Draw.setText("Draw");
			Draw.setActionCommand("Draw");
			Stay.setText("Stand");
			Stay.setActionCommand("Stay");
			
		}
	
//------------------------------------------------------------------------------------------------------------//
			
			if (cardCheck.equals("Draw")) 
			{
					
					Draw.setText("Draw");

					if (newGame == true) {  //will only run once per round because newGame will be set to false later on. 
						newGame(); //will check for split conditions and Aces as well. 	
					}
					else {
						updatePlayerCard(); //if not a new game	
					}
					
					newGame = false; 
					
					//update output:

					labelText += " Your current Card Total is " + playerTotal; 
					label1.setText(labelText);
					
					//if anyone wins or loses etc.
						outcome();
			}
			
//------------------------------------------------------------------------------------------------------------//

			if (cardCheck.equals("DoubleDown")) {
				
				doubleDown = true;

				if (newGame == true) {
					
					label1.setText("You can only double down once the round has started.");
					
				}
				
				else { //newGame false
					
					updatePlayerCard();
					computerTurn();
					System.out.println("BEFORE" + splitRound);
					outcome();
					System.out.println("AFTER" + splitRound);
				}
				
				//if there is a split, these buttons will have their normal action commands.
				Draw.setText("Draw");
				Draw.setActionCommand("Draw");
				Stay.setText("Stand");
				Stay.setActionCommand("Stay");
				
			}
			
//------------------------------------------------------------------------------------------------------------//

			if (cardCheck.equals("doSplit")) {
				
					if (splitRound == 1) {
						
						updatePlayerCard();
						
						labelText += " Your current Card Total is " + playerTotal; 
						label1.setText("Split round 1: " + labelText);
						
						outcome();
						
					}
					
					else if (splitRound == 2) {
						if (newGame == true) {
							for (int i = 1; i < 6; i++) 
								playerCards[i].setIcon(newImg);
							Stay.setBackground(Color.WHITE);
							DoubleDown.setBackground(Color.WHITE);
							
							Draw.setText("Round 2 Draw!");
							playerTotal = card2Val; //doesn't matter if i use card1Val or card2Val.
							currentCard = 2;
							CardHold.setIcon(newCardHold);
							Card1.setIcon(card2Icon);
							
						//Set up computer card. Taken from the newGame() method. 
							cTotal = 0;
							cCard = 1;
							for (int i = 0; i < 6; i++) 
								computerCards[i].setIcon(cNewImg_BACKUP);
							
							try {cCard1.setIcon(getCard());} catch (IOException e1) {e1.printStackTrace();}
							cTotal += number;
							cCard = 2;	//make cCard is in the correct place. 

							label2.setText("Computer Output: Waiting Your Turn!");
							
							newGame = false;

						}
						
						
					//same thing
						updatePlayerCard();
						
						labelText += " Your current Card Total is " + playerTotal; 
						label1.setText("Split round 2: " + labelText);
						
						outcome();	
				}
				
			}
//------------------------------------------------------------------------------------------------------------//		
		if (cardCheck.equals("YesInsurance")) { 
			
			//computer goes
			try {cCard2.setIcon(getCard());} catch (IOException e1) {e1.printStackTrace();}
			cTotal += number;
			cCard++;	
			
			if (cTotal == 21) {
				bbucks += bet; //return your insurance, then its a 2 to 1 pay out. 
				label3.setText("Nice prediction! Your current total is :" + bbucks);
				newGame = true; 
				Draw.setText("Start Round");
			}
			
			else { //not 21
				bbucks -= bet/2; //you lose half of your bet. 
				label3.setText("Sike! The game shall continue... you lost " + bet/2 + ". Your current total is :" + bbucks);
				Draw.setText("Draw");
			}//ends else, not 21
			//sets the draw button back to normal
			
			Draw.setActionCommand("Draw");
			Stay.setText("Stand");
			Stay.setActionCommand("Stay");
		}
		
		if (cardCheck.equals("NoInsurance")) { 
			//if there are identical cards, you then have the option to split.  
			if (card1Char == card2Char) {
				labelText += "You have matching cards. Would you like to split?";
				Stay.setText("No, do not split.");
				Draw.setText("Yes, perform a split.");
				Draw.setActionCommand("Split");
				Stay.setActionCommand("noSplit");
			}
			else {
				Draw.setText("Draw");
				Draw.setActionCommand("Draw");
				Stay.setText("Stand");
				Stay.setActionCommand("Stay");
			}
		}
		
			
//------------------------------------------------------------------------------------------------------------//		
		if (cardCheck.equals("Stay")) { 
		
			if (newGame == false){
				computerTurn();
				
				if (cTotal > 21)
					win();
					
				else if (playerTotal < cTotal)
					lose();
				
				else if (playerTotal > cTotal)
					win();
				
				else 
					tie();
				
			label1.setText(labelText); //update label
			}
			
		}//Stay
				
//------------------------------------------------------------------------------------------------------------//		
		if (cardCheck.equals("Input")) { 
			//create a fail-safe somewhere that gives u some money when u lose. (aka when ur bbucks is at 0).
		
		if ((newGame == true) && (splitWagerChange == true)){
			
			bet = (int)Integer.parseInt(input.getText());
					
			
			if (bet < 0) {
				labelText = "You can't gain money when you lose ... Please enter another number";
				bet = 0;
			}
			
			else if (bet > bbucks) {
				bet = 0;
				labelText = "Umm... you can't bet more than you have. Your bet has been changed to" + bet;
			}
				
			else if (bet >= 0){ //correct input
				labelText = "Your bet has been set to $" + bet;
			}	
			
			else {
				bet = 0;
				labelText = "Invalid Input. Your bet has been set to $" + bet;
			}
			
			
			labelText += ". You now have $" + bbucks;
			label3.setText(labelText); //update label
			
		}//if new game = true
		
		else
			label3.setText("You can not change your wager mid game. THAT'S CHEATING! Your current wager is $" + bet + "."); //update label
			
		}//Input
		
//------------------------------------------------------------------------------------------------------------//		
		if (cardCheck.equals("changeTheme")) {
			
			if ((theme % 3) == 2) {
				img = new ImageIcon("blue_back.png");
				Image resized = img.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				newImg = new ImageIcon(resized);				
			}
			if ((theme % 3) == 1) {
				img = new ImageIcon("green_back.png");
				Image resized = img.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				newImg = new ImageIcon(resized);
			}
			if ((theme % 3) == 0) {
				img = new ImageIcon("purple_back.png");
				Image resized = img.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				newImg = new ImageIcon(resized);
			}
			
			cardHoldResized = img.getImage().getScaledInstance(90, 150, java.awt.Image.SCALE_AREA_AVERAGING);
			newCardHold = new ImageIcon(cardHoldResized);
			
			for (int i = currentCard - 1; i < 6; i++)
				playerCards[i].setIcon(newImg);
			CardHold.setIcon(newCardHold);
			
			theme++;
	}
		
//------------------------------------------------------------------------------------------------------------//		
	if (cardCheck.equals("Tutorial")) {
			if (newGame == false) {
				
				label1.setText("Please click once the round is over. " + "Your total at the moment is " + playerTotal);
			}
			
			else {
				//refresh cards
				for (int i = 0; i < 6; i++)
					playerCards[i].setIcon(newImg);
				for (int i = 0; i < 6; i++)
					computerCards[i].setIcon(cNewImg);
				//tutorial cards
				ImageIcon t; //raw picture
				Image tR; //resized picture
				ImageIcon T; //resized picture as icon
				
				t = new ImageIcon("Tutorial_1.png");
				tR = t.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				T = new ImageIcon(tR);
				Card1.setIcon(T);
				
				t = new ImageIcon("Tutorial_2.png");
				tR = t.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				T = new ImageIcon(tR);
				Card2.setIcon(T);
				
				t = new ImageIcon("Tutorial_3.png");
				tR = t.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				T = new ImageIcon(tR);
				Card3.setIcon(T);
				
				t = new ImageIcon("Tutorial_4.png");
				tR = t.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				T = new ImageIcon(tR);
				Card4.setIcon(T);
				
				t = new ImageIcon("Tutorial_5.png");
				tR = t.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
				T = new ImageIcon(tR);
				Card5.setIcon(T);
				
				currentCard = 6; //this is so the changetheme won't mess the tutorial cards up.
			}		
			
		}
		
	}//action listener

	
				//***********************-----------------METHODS-----------------***********************//
	
	
//Update Card
	public void updatePlayerCard() {
		
				
	//update the card value and image in the corresponding card object (Again)
		
		try {playerCards[currentCard - 1].setIcon(getCard());} catch (IOException e) {e.printStackTrace();}

		System.out.println("The value of card " + (currentCard) + " is " + number); //debug
		
		playerTotal += number;
		
		if (number == 11)
			softHand = true;
	
		currentCard++;
		
		labelText  = cardText;
		
	}

//Set up cards and values
	public void newGame() {
		
		Stay.setBackground(Color.WHITE);
		DoubleDown.setBackground(Color.WHITE);
		
		softHand = false;
		doSplit = false;
		doubleDown = false;
		playerTotal = 0;
		currentCard = 1; 
		card1Val = 0;
		card2Val = 0;
		for (int i = 0; i < 6; i++) 
			playerCards[i].setIcon(newImg);
		
		//reset computer cards
		cTotal = 0;
		cCard = 1;
		for (int i = 0; i < 6; i++) 
			computerCards[i].setIcon(cNewImg_BACKUP);
		
		String newGameLabelText = "";
		
		//player: 2 cards dealt: Output, copy card number to card1Val and card2val, add to player total, increment currentCard
		//card 1
		try {Card1.setIcon(getCard());} catch (IOException e1) {e1.printStackTrace();}
		newGameLabelText = ("Your first card:  " + cardText);
		card1Val = number;
		card1Char = cardText.charAt(0);
		playerTotal += number;
		currentCard++; //currentCard = 2
		System.out.println("The value of card " + (currentCard) + "is " + number); //debug
		
		//card 2
		
		try {Card2.setIcon(getCard());} catch (IOException e1) {e1.printStackTrace();}
		card2Icon = (ImageIcon) Card2.getIcon();
		card2ImageSmall = card2Icon.getImage().getScaledInstance(90, 150, java.awt.Image.SCALE_AREA_AVERAGING);
		card2IconSmall = new ImageIcon(card2ImageSmall);
		
		newGameLabelText += " Second Card: " + cardText;
		labelText = newGameLabelText; //copies over.
		card2Val = number;
		card2Char = cardText.charAt(0);
		playerTotal += number;
		currentCard++; //currentCard = 3
		System.out.println("The value of card " + (currentCard) + "is " + number); //debug
	

		label1.setText(labelText);
		
		//check for soft hand
		if ((card1Val == 11) || (card2Val == 11)) 
			softHand = true; //the XOR is there so as to not interfere with the split. 
			
		
		 
	//computer: 1 card dealt. Huh, kinda contradictory cuz this is supposed to be the player card update... lol	
		try {cCard1.setIcon(getCard());} catch (IOException e) {e.printStackTrace();}
		cTotal += number;
		cCard = 2;	//make sure cCard is in the correct place. 

		label2.setText("Computer Output: Waiting Your Turn!");
	
		bbucks -= bet;
		label3.setText("You have just placed your bet of " + bet + ". Your total is $" + bbucks);

	//THE TWO SPECIAL CONDITIONS: INSURANCE OR SPLIT
		
		//check for split
		if (card1Char == card2Char) {
			labelText += "You have matching cards. Would you like to split?";
			Stay.setText("No, do not split.");
			Draw.setText("Yes, perform a split.");
			Draw.setActionCommand("Split");
			Stay.setActionCommand("noSplit");
		}
		/*the insurance has precedent over split. If you have two identical cards and CPU has ace, 
		the buttons will be changed from split action to insurance */
		if (cTotal == 11) { //computer gets an ace and the two original cards are different.
			insurance = true;
			labelText = newGameLabelText; //removes the previous labelText, in case there is a split.
			Draw.setActionCommand("YesInsurance");
			Stay.setActionCommand("NoInsurance");
			Draw.setText("Buy insurnace");
			Stay.setText("Don't buy insurance");
			label2.setText("Computer receieved an Ace. Would you like to buy insurance? (Half of current bet)");
				
		}
	}//newGame

	
//LOSE
	public void lose() {
		
		if (bbucks <= 0) {	
			bbucks += 1000;
			label3.setText("Bankrupt? Let me loan you some Blackjack Bucks! You now have $" + bbucks);
		}
		
			
		else {
			if (doubleDown == false)
				label3.setText("You lost your bet of $" + bet + ". Your current total is $" + bbucks); 

			else if (doubleDown == true) 
				bbucks -= bet;
				label3.setText("Uh oh... You lost  $" + bet + ". Your current total is $" + bbucks);
		}

			
		labelText += "Your total is " + playerTotal + " Press \"Hit\" to start next round ";
		label2Text += " \n The Computer Has BESTED YOU! ";
		Draw.setText("Start New Round");
	
		newGame = true;
		Stay.setBackground(Color.LIGHT_GRAY);
		DoubleDown.setBackground(Color.LIGHT_GRAY);
		
		//FROM HERE BELOW, THIS ONLY PERTAINS TO THE SPLIT FUNCTION
		if (splitRound == 2) {  
			doSplit = false;
			Draw.setActionCommand("Draw");
			splitWagerChange = true;
		}
		splitRound++; 
				
	}//lose
	
//WIN
	public void win() {
		
		if (doubleDown == false) {
			bbucks += bet * 2;
			labelText = "Wow, you are Goood. Press \"Hit\" for another round ";
			label3.setText("You gained $" + bet + "! Your current total is $" + bbucks);
		}
		
		if (doubleDown == true) {
			bbucks += bet * 3;
			labelText = "Absolutely AMAZING!. Press \"Hit\" for another round ";
			label3.setText("You Profited $" + bet * 2 + "! Your current total is $" + bbucks);
		}
		
		newGame = true;
		Stay.setBackground(Color.LIGHT_GRAY);
		DoubleDown.setBackground(Color.LIGHT_GRAY);

		Draw.setText("Start New Round");
		
		//FROM HERE BELOW, THIS ONLY PERTAINS TO THE SPLIT FUNCTION
		if (splitRound == 2) {  
			doSplit = false;
			Draw.setActionCommand("Draw");
			splitWagerChange = true;
		}
		splitRound++; 

	}//win
	
//TIE
	public void tie() {
		
		bbucks += bet; //bet is returned
		
		labelText = "Ahhh a tie. That was close. Press \"Hit\" for another round ";
		Draw.setText("Start New Round");
		
		newGame = true;
		Stay.setBackground(Color.LIGHT_GRAY);
		DoubleDown.setBackground(Color.LIGHT_GRAY);

		//FROM HERE BELOW, THIS ONLY PERTAINS TO THE SPLIT FUNCTION
		if (splitRound == 2) {  
			doSplit = false;
			Draw.setActionCommand("Draw");
			splitWagerChange = true;
		}
		splitRound++; 
			
		}//tie
	
//WIN/LOSE/TIE method
	public void outcome() {
		
	//WIN OR LOSE CONDITIONS
		if ((playerTotal) == 21) {
			
			label1.setText("Wow BlackJack! I wonder what the computer will get...");
			computerTurn(); //computer turn contains the updateComputerCard method. 
	
			if(cTotal == 21) {
				tie();
				label1.setText("Its a tie...what luck. Press \"Hit\" to start next round");
			}
			
			else {
				win();
				label1.setText("Nice! How about another round?");
			
			}
		}//if player total == 21
	
		
		//during the case of a soft hand, and you bust, and u chose double down. 
		else if ((playerTotal > 21) && (softHand == true) && (doubleDown == true)) {
			playerTotal -= 10;
			softHand = false;
			label1.setText("Oh... a soft hand! Your ace has been changed to a value of 1. Your current total is " + playerTotal);
			//outcome is then decided, cuz u chose double down
			if (playerTotal < cTotal)
				lose();
			
			else if (playerTotal > cTotal)
				win();
			
			else if (playerTotal == cTotal)
				tie();
		}
		
		else if ((playerTotal > 21) && (softHand == true)) {
			playerTotal -= 10;
			softHand = false;
			//you don't lose. 
			label1.setText("Oh... a soft hand! Your ace has been changed to a value of 1. Your current total is " + playerTotal);
		}
		
		else  if (playerTotal > 21) {
			//lose
			labelText += "...Bust! ";
			lose();
			label1.setText(labelText); //update label

		}//if player total > 21
		//if no soft hand, if no bust:
		else if (doubleDown == true) {//if the player has hit the button for double down. 
			if (cTotal > 21)
				win();
				
			else if (playerTotal < cTotal)
				lose();
			
			else if (playerTotal > cTotal)
				win();
			
			else if (playerTotal == cTotal)
				tie();
			
		label1.setText(labelText); //update label		
		
		}//else if doubleDown
		
	}

//COMPUTER TURN
	public void computerTurn() {
		//17 or more, must stand. 
		
		while (cTotal < 17) {
			
			try {computerCards[cCard-1].setIcon(getCard());} catch (IOException e) {e.printStackTrace();}
			cTotal += number;
			cCard++;
		}//while
		
		label2Text = ("Computer Total is now " + cTotal);
		label2.setText(label2Text);
		
	}//computerTurn
	
//PICK CARD

	public ImageIcon getCard() throws IOException {
		
		Image card;
		int suite; //1 to 4: 1 = Spades, 2 = Hearts, 3 = Clubs, 4 = Diamonds
		
		Random genCard= new Random(); //randomizer object called genCard
	
		//CARD NUMBER
		number = genCard.nextInt(12+1) + 1; //1 - 13;
		//number = genCard.nextInt(2) + 6; //1 - 13; //used to test split 

		//CARD SUITE
		suite = genCard.nextInt(3+1) + 1; //1 - 4
		
		System.out.println(suite + "yee " + number);
		

	//*****CARD ASSIGNMENT*****//
		//Spades: Suite = 1, Number from 1 to 13
	
			if ((suite == 1)&&(number == 1)) 
			{
				card = ImageIO.read(new File("AS.png"));
				cardText = "Ace of Spades!";
				number = 11;
 			}
			
			else if ((suite == 1)&&(number == 2)) 
			{
				card = ImageIO.read(new File("2S.png"));
				cardText = "2 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 3)) 
			{
				card = ImageIO.read(new File("3S.png"));
				
				cardText = "3 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 4)) 
			{
				card = ImageIO.read(new File("4S.png"));
				cardText = "4 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 5)) 
			{
				card = ImageIO.read(new File("5S.png"));
				cardText = "5 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 6)) 
			{
				card = ImageIO.read(new File("6S.png"));
				cardText = "6 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 7)) 
			{
				card = ImageIO.read(new File("7S.png"));
				cardText = "7 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 8)) 
			{
				card = ImageIO.read(new File("8S.png"));
				cardText = "8 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 9)) 
			{
				card = ImageIO.read(new File("9S.png"));
				cardText = "9 of Spades!";
 			}
			
			else if ((suite == 1)&&(number == 10)) 
			{
				card = ImageIO.read(new File("10S.png"));
				cardText = "10 of Spades!";
				number = 10;

			}
			
			else if ((suite == 1)&&(number == 11)) 
			{
				card = ImageIO.read(new File("JS.png"));
				cardText = "Jack of Spades!";
				number = 10;
			}
			
			else if ((suite == 1)&&(number == 12)) 
			{
				card = ImageIO.read(new File("QS.png"));
				cardText = "Queen of Spades!";
				number = 10;
 			}
			
			else if ((suite == 1)&&(number == 13)) 
			{
				card = ImageIO.read(new File("KS.png"));
				cardText = "King of Spades!";
				number = 10;
			}
			
		//Hearts: Suite = 2, Number from 1 to 13
			else if ((suite == 2)&&(number == 1)) 
			{
				card = ImageIO.read(new File("AH.png"));
				cardText = "Ace of Hearts!";
				number = 11;
 			}
			
			else if ((suite == 2)&&(number == 2)) 
			{
				card = ImageIO.read(new File("2H.png"));
				cardText = "2 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 3)) 
			{
				card = ImageIO.read(new File("3H.png"));
				cardText = "3 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 4)) 
			{
				card = ImageIO.read(new File("4H.png"));
				cardText = "4 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 5)) 
			{
				card = ImageIO.read(new File("5H.png"));
				cardText = "5 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 6)) 
			{
				card = ImageIO.read(new File("6H.png"));
				cardText = "6 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 7)) 
			{
				card = ImageIO.read(new File("7H.png"));
				cardText = "7 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 8)) 
			{
				card = ImageIO.read(new File("8H.png"));
				cardText = "8 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 9)) 
			{
				card = ImageIO.read(new File("9H.png"));
				cardText = "9 of Hearts!";
 			}
			
			else if ((suite == 2)&&(number == 10)) 
			{
				card = ImageIO.read(new File("10H.png"));
				cardText = "10 of Hearts!";
				number = 10;
 			}
			
			else if ((suite == 2)&&(number == 11)) 
			{
				card = ImageIO.read(new File("JH.png"));
				cardText = "Jack of Hearts!";
				number = 10;
 			}
			
			else if ((suite == 2)&&(number == 12)) 
			{
				card = ImageIO.read(new File("QH.png"));
				cardText = "Queen of Hearts!";
				number = 10;
 			}
			
			else if ((suite == 2)&&(number == 13)) 
			{
				card = ImageIO.read(new File("KH.png"));
				cardText = "King of Hearts";
				number = 10;
 			}

		//Clubs: Suite = 3, Number from 1 to 13
			else if ((suite == 3)&&(number == 1)) 
			{
				card = ImageIO.read(new File("AC.png"));
				cardText = "Ace of Clubs!";
				number = 11;
			}
			
			else if ((suite == 3)&&(number == 2)) 
			{
				card = ImageIO.read(new File("2C.png"));
				cardText = "2 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 3)) 
			{
				card = ImageIO.read(new File("3C.png"));
				cardText = "3 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 4)) 
			{
				card = ImageIO.read(new File("4C.png"));
				cardText = "4 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 5)) 
			{
				card = ImageIO.read(new File("5C.png"));
				cardText = "5 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 6)) 
			{
				card = ImageIO.read(new File("6C.png"));
				cardText = "6 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 7)) 
			{
				card = ImageIO.read(new File("7C.png"));
				
				cardText = "7 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 8)) 
			{
				card = ImageIO.read(new File("8C.png"));
				cardText = "8 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 9)) 
			{
				card = ImageIO.read(new File("9C.png"));
				cardText = "9 of Clubs!";
			}
			
			else if ((suite == 3)&&(number == 10)) 
			{
				card = ImageIO.read(new File("10C.png"));
				cardText = "10 of Clubs!";
				number = 10;
			}
			
			else if ((suite == 3)&&(number == 11)) 
			{
				card = ImageIO.read(new File("JC.png"));
				cardText = "Jack of Clubs!";
				number = 10;
			}
			
			else if ((suite == 3)&&(number == 12)) 
			{
				card = ImageIO.read(new File("QC.png"));
				cardText = "Queen of Clubs!";
				number = 10;
			}
			
			else if ((suite == 3)&&(number == 13)) 
			{
				card = ImageIO.read(new File("KC.png"));
				cardText = "King of Clubs!";
				number = 10;
			}
			
		//Diamonds: Suite = 4, Number from 1 to 13
			else if ((suite == 4)&&(number == 1)) 
			{
				card = ImageIO.read(new File("AD.png"));
				cardText = "Ace of Diamonds!";
				number = 11;
			}
			
			else if ((suite == 4)&&(number == 2)) 
			{
				card = ImageIO.read(new File("2D.png"));
				cardText = "2 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 3)) 
			{
				card = ImageIO.read(new File("3D.png"));
				cardText = "3 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 4)) 
			{
				card = ImageIO.read(new File("4D.png"));
				cardText = "4 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 5)) 
			{
				card = ImageIO.read(new File("5D.png"));
				cardText = "5 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 6)) 
			{
				card = ImageIO.read(new File("6D.png"));
				cardText = "6 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 7)) 
			{
				card = ImageIO.read(new File("7D.png"));
				cardText = "7 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 8)) 
			{
				card = ImageIO.read(new File("8D.png"));
				cardText = "8 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 9)) 
			{
				card = ImageIO.read(new File("9D.png"));
				cardText = "9 of Diamonds!";
			}
			
			else if ((suite == 4)&&(number == 10)) 
			{
				card = ImageIO.read(new File("10D.png"));
				cardText = "10 of Diamonds!";
				number = 10;
			}
			
			else if ((suite == 4)&&(number == 11)) 
			{
				card = ImageIO.read(new File("JD.png"));
				cardText = "Jack of Diamonds!";
				number = 10;
			}
			
			else if ((suite == 4)&&(number == 12)) 
			{
				card = ImageIO.read(new File("QD.png"));
				cardText = "Queen of Diamonds!";
				number = 10;

			}
			
			else if ((suite == 4)&&(number == 13)) 
			{
				card = ImageIO.read(new File("KD.png"));
				cardText = "King of Diamonds!";
				number = 10;
			}	
			
			else 
			{
				card = ImageIO.read(new File("galaxy-wallpaper-2.jpg"));
				cardText = "Uhhh... oops";
				
			}
			
		//take the image and resize it. 
		ImageIcon img1 = new ImageIcon(card);
		Image resized1 = img1.getImage().getScaledInstance(180, 300, java.awt.Image.SCALE_AREA_AVERAGING);
		ImageIcon newImg1 = new ImageIcon(resized1);
		
		return newImg1;
	}

}

package com.iluwatar.hexagonal.service;

import com.google.inject.Inject;
import com.iluwatar.hexagonal.banking.WireTransfers;
import com.iluwatar.hexagonal.domain.*;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Console implementation for lottery console service
 */
public class LotteryConsoleServiceImpl implements LotteryConsoleService {

  private final Logger logger;

  /**
   * Constructor
   */
  public LotteryConsoleServiceImpl(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void checkTicket(LotteryService service, Scanner scanner) {
    logger.info( "What is the ID of the lottery ticket?" );
    String id = readString( scanner );
    logger.info( "Give the 4 comma separated winning numbers?" );
    String numbers = readString( scanner );
    try {
      String[] parts = numbers.split( "," );
      Set<Integer> winningNumbers = new HashSet<>();
      for (int i = 0; i < 4; i++) {
        winningNumbers.add( Integer.parseInt( parts[i] ) );
      }

      final LotteryTicketId lotteryTicketId = new LotteryTicketId( Integer.parseInt( id ) );
      final LotteryNumbers lotteryNumbers = LotteryNumbers.create( winningNumbers );
      LotteryTicketCheckResult result = service.checkTicketForPrize( lotteryTicketId, lotteryNumbers );

      if (result.getResult().equals( LotteryTicketCheckResult.CheckResult.WIN_PRIZE )) {
        logger.info( "Congratulations! The lottery ticket has won!" );
      } else if (result.getResult().equals( LotteryTicketCheckResult.CheckResult.NO_PRIZE )) {
        logger.info( "Unfortunately the lottery ticket did not win." );
      } else {
        logger.info( "Such lottery ticket has not been submitted." );
      }
    } catch (Exception e) {
      logger.info( "Failed checking the lottery ticket - please try again." );
    }
  }

  @Override
  public void submitTicket(LotteryService service, Scanner scanner) {
    logger.info( "What is your email address?" );
    String email = readString( scanner );
    logger.info( "What is your bank account number?" );
    String account = readString( scanner );
    logger.info( "What is your phone number?" );
    String phone = readString( scanner );
    PlayerDetails details = new PlayerDetails( email, account, phone );
    logger.info( "Give 4 comma separated lottery numbers?" );
    String numbers = readString( scanner );
    try {
      String[] parts = numbers.split( "," );
      Set<Integer> chosen = new HashSet<>();
      for (int i = 0; i < 4; i++) {
        chosen.add( Integer.parseInt( parts[i] ) );
      }
      LotteryNumbers lotteryNumbers = LotteryNumbers.create( chosen );
      LotteryTicket lotteryTicket = new LotteryTicket( new LotteryTicketId(), details, lotteryNumbers );
      Optional<LotteryTicketId> id = service.submitTicket( lotteryTicket );
      if (id.isPresent()) {
        logger.info( "Submitted lottery ticket with id: {}", id.get() );
      } else {
        logger.info( "Failed submitting lottery ticket - please try again." );
      }
    } catch (Exception e) {
      logger.info( "Failed submitting lottery ticket - please try again." );
    }
  }

  @Override
  public void addFundsToLotteryAccount(WireTransfers bank, Scanner scanner) {
    logger.info( "What is the account number?" );
    String account = readString( scanner );
    logger.info( "How many credits do you want to deposit?" );
    String amount = readString( scanner );
    bank.setFunds( account, Integer.parseInt( amount ) );
    logger.info( "The account {} now has {} credits.", account, bank.getFunds( account ) );
  }

  @Override
  public void queryLotteryAccountFunds(WireTransfers bank, Scanner scanner) {
    logger.info( "What is the account number?" );
    String account = readString( scanner );
    logger.info( "The account {} has {} credits.", account, bank.getFunds( account ) );
  }

  private String readString(Scanner scanner) {
    System.out.print( "> " );
    return scanner.next();
  }
}

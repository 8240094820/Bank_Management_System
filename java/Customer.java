package com.pRoject.java;

import java.util.Scanner;

class bank{
	private double bal = 5000.00;
	private int PWD;
	
	public void Deposit(double money) {
		System.out.println("Enter the password");
		Scanner sc= new Scanner(System.in);
		PWD=sc.nextInt();
		if(PWD==123) {
			bal=bal+money;
		}else {
			System.out.println("Wrong password...");
		}
		System.out.println("You deposited "+money+" rupees");
		System.out.println("Your current balance is "+bal);
	}
	public void Withdrow(double money) {
		System.out.println("Enter the password");
		Scanner sc= new Scanner(System.in);
		PWD=sc.nextInt();
		if(PWD==123) {
			if(bal>money) {
				bal=bal-money;
				System.out.println("You withdrow "+money+" rupees");
				System.out.println("Your current balance is "+bal);
			}else {
				System.out.println("You don't have sufficient balance...");
			}
		}else {
			System.out.println("Wrong password...");
		}
		
	}
	public void Balance() {
		System.out.println("Your current balance is "+bal);
	}
	
}
public class Customer {

	public static void main(String[] args) {
	bank b = new bank();
	int ch;
	System.out.println("Chose the option...");
	System.out.println("1. Deposit");
	System.out.println("2. Withdrow");
	System.out.println("3. Balance");
	System.out.println("-----------------------------------------------");
	Scanner sc = new Scanner(System.in);
	ch=sc.nextInt();
	switch(ch) {
	case 1:
		System.out.println("Enter the amount...");
		Scanner sc1=new Scanner(System.in);
		double money=sc1.nextInt();
		b.Deposit(money);
		break;
	case 2:
		System.out.println("Enter the amount...");
		Scanner sc2=new Scanner(System.in);
		double money2=sc2.nextInt();
		b.Withdrow(money2);
		break;
	case 3:
		b.Balance();
		break;
	default:
		System.out.println("Please enter valid number...");
	}
		sc.close();
	}
	

}

#Sipper

## Synopsis

An Android SIP Client that can be used to register with a SIP server and make, receive, hold and resume audio calls.

It consumes the Doubango Android NGN Stack, which is also open source, for SIP functionality.

## Motivation

This project can be used as a demonstration of the usage of the Doubango NGN SIP Stack.

## Installation

- In Eclipse, select File -> Import -> Git -> Projects from Git -> Local.
- Select the location of this project on your hard drive and import it.

Note: You must also import the android-support-v7-appcompat library project and the android-ngn-stack library project (which I have provided) into your Eclipse workspace if it is not already present. 

Importing the android-support-v7-appcompat project can be done  like so:
- In Eclipse, Select File -> Import -> Android -> Existing Android Code Into Workspace.
- Browse to the android-support-v7-appcompat location. This should be in <ADT location>\sdk\extras\android\support\v7\appcompat

Importing the android-ngn-stack project can be done like so:
- Move or copy the android-ngn-stack folder out of the Sipper folder. Place it in the same folder as the Sipper folder.
- In Eclipse, Select File -> Import -> Android -> Existing Android Code Into Workspace.
- Browse to the android-ngn-stack location.

## License

Sipper is licensed under the GNU GPL v3 open source license.

The Doubango Android NGN STack, which is consumed by Sipper, is also licensed under the GNU GPL v3 open source license. 

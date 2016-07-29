package models 

case class Contact(
	contactID: Option[Int],
	username: String,
	number: String,
	userID: Int
	)
package models 

case class User(
	userID: Option[Int],
	username: String,
	password: String
	)
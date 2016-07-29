package models

import javax.inject. { Inject, Singleton }
import play.api.db.slick. { DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.profile.RelationalProfile
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Contacts @Inject()(
		val dbConfigProvider: DatabaseConfigProvider
	)extends HasDatabaseConfigProvider[RelationalProfile]{

		import slick.driver.PostgresDriver.api._

		val contacts = TableQuery[ContactsTable]

		def allContacts: Future[Seq[Contact]] = db.run( contacts.result )

		def insertContact(contact: Contact): Future[Boolean] =
			db.run( contacts += contact ).map( _ > 0 )

		def deleteContact(id: Int): Future[Boolean] = 
			db.run( contacts.filter( _.contactID === id ).delete.map( _ > 0 ) )

		def updateContact(contact: Contact): Future[Boolean] =
			db.run( contacts.filter( _.contactID === contact.contactID ).update( contact ).map( _ > 0 ) )

		def findById(id: Int) : Future[Option[Contact]] =
			db.run( contacts.filter( _.contactID === id ).result.headOption )

		class ContactsTable(tag: Tag) extends Table[Contact](tag, "CONTACTS"){

			def contactID = column[Int]("contactid", O.PrimaryKey, O.AutoInc)
			def username = column[String]("username")
			def number = column[String]("number")
			def userID = column[Int]("userid")

			def * = ( contactID.?, username, number, userID ) <> ( Contact.tupled, Contact.unapply )

		}
	}
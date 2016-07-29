package models

import javax.inject. { Inject, Singleton }
import play.api.db.slick. { DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.profile.RelationalProfile
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Users @Inject()(
		val dbConfigProvider: DatabaseConfigProvider
	)extends HasDatabaseConfigProvider[RelationalProfile]{

		import slick.driver.PostgresDriver.api._ 

		val users = TableQuery[UsersTable] 

		def all: Future[Seq[User]] = db.run( users.result )

		def findByUsername(username: String) : Future[Option[User]] =
			db.run( users.filter( _.username === username ).result.headOption )
		

		def validatePass(username:String, password: String): Future[Option[User]] = { 
			db.run( users.filter( user => user.username === username && user.password === password ).result.headOption )
		}


		def insertUser(user: User): Future[Boolean] =
			db.run( users += user ).map( _ > 0 )
			
		class UsersTable(tag: Tag) extends Table[User](tag, "USERS"){

			def userID = column[Int]("userid", O.PrimaryKey, O.AutoInc)
			def username = column[String]("username")
			def password = column[String]("password")

			def * = ( userID.?, username, password ) <> ( User.tupled, User.unapply )

		}

	}
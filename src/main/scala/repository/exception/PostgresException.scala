package repository.exception

import scala.util.control.NoStackTrace

sealed trait PostgresException extends NoStackTrace:
  val postgresErrorCode: String

object PostgresException:
  object UniqueConstraintViolation extends PostgresException:
    override val postgresErrorCode: String = "23505"
    
  object ForeignKeyConstraintViolation extends PostgresException:
    override val postgresErrorCode: String = "23503"
    
  case class UnmappedPostgresException(override val postgresErrorCode: String) extends PostgresException
  
  def apply(postgresErrorCode: String): PostgresException = postgresErrorCode match
    case "23505" => UniqueConstraintViolation
    case "23503" => ForeignKeyConstraintViolation
    case code => UnmappedPostgresException(code)
import java.lang.Exception
// idea for future extension: extend error classes so that they pinpoint where's the error
class BadFilePathException extends Exception("Specified location doesn't exist.")
case class CorruptedFileException(description: String) extends RuntimeException(description)
case class UnknownException(description: String) extends Exception(description)
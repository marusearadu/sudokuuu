import java.lang.Exception

case class BadFilePathException(description: String, data: String)  extends Exception(description)
case class CorruptedFileExcption(description: String, data: String) extends Exception(description)
// case class
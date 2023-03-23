package game

case class CorruptedFileException(description: String) extends RuntimeException(description)

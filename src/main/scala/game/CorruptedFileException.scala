package game

/** Exception class; thrown when the given file is (suspected to be) corrupted. */
case class CorruptedFileException(description: String) extends RuntimeException(description)

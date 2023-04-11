package game

/** Exception class; thrown when no other exceptions fits the criteria. */
case class UnknownException(description: String) extends Exception(description)

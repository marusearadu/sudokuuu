package game

/** Exception class; thrown when the given file path doesn't exist/contains a non-json file. */
class BadFilePathException extends Exception("Specified location doesn't exist or doesn't represent an readable file.")

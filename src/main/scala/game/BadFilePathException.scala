package game

// idea for future extension: extend error classes so that they pinpoint where's the error
class BadFilePathException extends Exception("Specified location doesn't exist or doesn't represent an readable file.")

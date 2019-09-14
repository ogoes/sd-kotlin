# Communication Protocol (peer/name-server)

##### Using Big-Endian Notation

## Connect and Register nickname

-   REQUEST

    | 1 byte  |    1 byte     | (1-256) bytes |    1 byte    | (1-256) bytes | 4 bytes |
    | :-----: | :-----------: | :-----------: | :----------: | :-----------: | :-----: |
    | type(1) | nickname size |   nickname    | address size |    address    |  port   |

-   RESPONSE

    | 1 byte  |         1 byte          |
    | :-----: | :---------------------: |
    | type(1) | SUCCESS(1) - FAILURE(2) |

## Request all connected host nicknames

-   REQUEST

    | 1 byte  |
    | :-----: |
    | type(2) |

-   RESPONSE

    | 1 byte  |         1 byte          |          1 byte           |
    | :-----: | :---------------------: | :-----------------------: |
    | type(2) | SUCCESS(1) - FAILURE(2) | number of connected hosts |

    ### append the follow bytes for each connected host:

    |        1 byte         |  (1-256) bytes   |
    | :-------------------: | :--------------: |
    | host(n) nickname size | host(n) nickname |

## Request host information by nickname

-   REQUEST

    | 1 byte  |       1 byte       | (1-256) bytes |
    | :-----: | :----------------: | :-----------: |
    | type(3) | host nickname size |   nickname    |

-   RESPONSE

    | 1 byte  |      1 byte       |  (1-256) bytes  |  4 bytes  |
    | :-----: | :---------------: | :-------------: | :-------: |
    | type(3) | host address size | host ip address | host port |

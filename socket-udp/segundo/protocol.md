# Communication Protocol (peer/file-server)

##### Using Big-Endian Notation

## File upload

- REQUEST

  |    1 byte     | 0 - 255  |  4 byte   |
  | :-----------: | :------: | :-------: |
  | namefile size | namefile | file size |

- RESPONSE

  | 1 byte |
  | :----: |
  |  type  | SUCCESS(1) - FAILURE(2) |

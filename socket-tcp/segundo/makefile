SERVER=server/main
CLIENT=client/main
COMP=kotlinc
ARG=-include-runtime -d

all: compServer compClient
	echo -n
runS: compServer
	java -jar server.jar ./.shared 9999

runC: compClient
	java -jar client.jar localhost 9999

compServer:
	$(COMP) server/main.kt server/Server.kt Interface.kt SocketConnection.kt $(ARG) server.jar

compClient:
	$(COMP) client/main.kt Interface.kt SocketConnection.kt $(ARG) client.jar



test:
	$(COMP) teste.kt $(ARG) teste.jar && java -jar teste.jar

clean: 
	rm *.jar

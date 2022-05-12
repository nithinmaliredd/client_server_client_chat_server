# Client-Server-Client Chat Server

### Description:

The main capability given to a client by this server is he can send a personal message to another client without being noticed by the other clients. Every client is given a id and this id is used to communicate with a particular client. The server is responsible for the transfer of message from one client to another.

The Backend is programmed using java sockets. The Frontend is operated using terminals.

### Protocol Details:

The server maintains the list of clients. A client can select another client from the list to communicate and send a message to the server. The server receives the messages from the client and forward the messages to the destination.

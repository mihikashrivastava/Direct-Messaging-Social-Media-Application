# ReadMe Phase 2

## Description

Welcome to the second phase of our Direct Messaging App. In this phase, we created a connection between the server class and the client class, allowing for communication and interaction through one server.

---

## MainClient.java

The `MainClient` class is responsible for managing the client-side flow of a user interacting with a social media platform. First, it establishes a connection to the server using a socket. The user is then prompted through a series of GUIs to either log in or sign up. If the user is new, they must enter a valid username, password, email, first name, and last name, ensuring that these inputs meet the required criteria: no commas, no spaces, and a minimum length of 8 characters.

After creating an account, a user must enter the username and password to log in. The system verifies their credentials, either accepting or rejecting the login attempt.

Once logged in, the client enters a loop where the user can send various commands to the server, such as blocking users or sending messages. The input is formatted with the logged-in username, and the command is sent to the server using a `PrintWriter`. The server's response is displayed in a dialog box, and the user is asked if they wish to continue. If they opt to exit, the client sends an "exit" command and terminates the connection. This method handles the full client-side logic for user registration, login, and interaction with the server, providing a smooth experience for managing user actions on the platform.

---

## MainServer.java

The `MainServer` class is the server-side component of the application. Based on specific commands from the `MessageManager` class sent to the server via the client, it performs the requested actions.

### Methods

| **Name**                | **Modifier** | **Return Type** | **Parameters**                 | **Description**                                                                                                                                          |
|-------------------------|--------------|-----------------|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `main`                 | Public       | Void            | None                           | Establishes a connection between the server and client via the port. Reads the command, and runs the corresponding `MessageManager` method.                |
| `searchUserFromUsername` | Public       | User            | String `username`             | Takes a username, searches the binary file storing all user objects, and returns the user if found. Otherwise, throws a `UserNotFoundException`.          |
| `processCommand`        | Public       | String          | String `inputFromClient`, `MessageManager messageManager` | Processes user commands related to messaging and user management. Invokes appropriate `MessageManager` methods and handles invalid inputs or exceptions. |

---

## How to Run

Before running, ensure that the files `accounts.txt` and `users.bin` exist. These files are required for the code to run. If they do not exist, create these files and leave them empty.

1. Launch the server application to initialize the server and allow it to accept incoming client connections.
2. Start the client application to establish a connection to the server.
3. Create user objects with details such as username, password, email, etc. Create at least **two new users**:
   - Create a user, log in from their perspective, and then send no commands.
   - Repeat the process for the second user.

4. Log in using the created usernames and passwords to access the portal for entering commands.

### Commands

Enter one of the following commands in the correct format (no spaces):

1. **`block`**: Blocks a user.
   - Format: `block,username1`
2. **`unblock`**: Unblocks a previously blocked user.
   - Format: `unblock,username1`
3. **`remove friend`** or **`remove`**: Removes a user from the friend list.
   - Format: `remove friend,username1` or `remove,username1`
4. **`add friend`** or **`add`**: Sends a friend request.
   - Format: `add friend,username1` or `add,username1`
5. **`accept friend`**: Accepts a friend request.
   - Format: `accept friend,username1`
6. **`reject friend`**: Rejects a friend request.
   - Format: `reject friend,username1`
7. **`send message`**: Sends a message to another user.
   - Format: `send message,username1,messageContent`

### Example Usage

If Tom is logged in and sends the command `block,jerry`, the system will block Jerry from Tom’s perspective.

Commands allow users to manage relationships and send messages effectively within the system.

---

## ClientHandler.java

The `ClientHandler` class implements multithreading to allow the direct messaging app to handle multiple clients simultaneously.

### Methods

| **Name** | **Modifier** | **Return Type** | **Parameters** | **Description**                                                                                           |
|----------|--------------|-----------------|----------------|-----------------------------------------------------------------------------------------------------------|
| `run`    | Public       | Void            | None           | Executes the `processCommand` method for a client's command. Sends the processed output back to the client. |

---

## Testing

The test utility file adds users to our accounts and user files to ensure that the test cases work properly.

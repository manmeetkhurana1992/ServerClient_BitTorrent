import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.nio.file.*;

public class Client3 {
	Socket requestSocket; 	// Socket to request chunks from Server
	Socket requestSocket2; 	// Socket to request chunks from Client2

	public static int noofparts = 0; // total number of chunks to be received in the end

	public void Client3() {
	}

	public static String Name = null;

	void receivefromserver() // to receive chunks from Server
	{
		try {
			// create a socket to connect to the server
			requestSocket = new Socket("localhost", 9050);
			System.out.println("Connected to localhost Server in port 9050");

			try {
				while (true) {
					InputStream input = requestSocket.getInputStream();
					noofparts = receiveChunk(input);
				}
			} catch (IOException ex) {
				System.out.println("All Chunks corresponding to Client3 have been received from Server");
			}

		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		}

		catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	void receivefromclient2() // to receive chunks from Client2
	{
		try {
			// create a socket to connect to the Client2
			requestSocket2 = new Socket("localhost", 9002);
			System.out.println("Connected to localhost Client 2 in port 9002 for 1st session.");

			try {
				while (true) {
					InputStream input = requestSocket2.getInputStream();
					receiveChunk(input);
				}
			} catch (IOException ex) {
				System.out.println("Chunks received from Client2");
			}

		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a Client 2 first.");
		}

		catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				requestSocket2.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	void receivefromclient2_2() // to receive chunks from Client2
	{
		try {
			// create a socket to connect to the Client2
			requestSocket2 = new Socket("localhost", 9002);
			System.out.println("Connected to localhost Client 2 in port 9002 again for 2nd session");

			try {
				while (true) {
					InputStream input = requestSocket2.getInputStream();
					receiveChunk(input);
				}
			} catch (IOException ex) {
				System.out.println("Chunks received from Client2 in session 2");
			}

		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a Client 2 first.");
		}

		catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				requestSocket2.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public static int sendtoclient4() {
		return noofparts; // to know number of chunks
	}

	private static final int cPort = 9003; // The Client3 will be listening on this port number for request from Client4

	public static void main(String args[]) throws Exception {
		Client3 client = new Client3();
		client.receivefromserver();
		client.receivefromclient2();

		ServerSocket listener = new ServerSocket(cPort);
		System.out.println("The Client 3 is listening for request from Client 4. Please start Client 4.");
		int parts = client.sendtoclient4();
		int ClientNum = 1;
		try {
			new Handler1(listener.accept(), ClientNum, parts, Name).start();
			ClientNum++;
			client.receivefromclient2_2();
			new Handler1(listener.accept(), ClientNum, parts, Name).start();
			ClientNum++;
		} finally {
			listener.close();
		}
		
		System.out.println("All " + parts + " chunks received by Client3. Now starting to merge these chunks.");
		String currentdir = new java.io.File(".").getCanonicalPath();
		List<File> l1 = new LinkedList<File>();
		String Name3 = Name.substring(0, Name.lastIndexOf('.'));
		File myfile = new File(currentdir, Name3);
		l1 = listOfFilesToMerge(myfile);
		mergeFiles(l1, myfile);
		System.out.println("Merging of all Chunks Complete.");
		System.out.println("You can find the merged file in Client3 Folder");
	}

	private static class Handler1 extends Thread {
		private Socket connection;
		private int no;
		private int noofparts;
		private String fileName;

		public Handler1(Socket connection, int no, int parts, String fileName) {
			this.connection = connection;
			this.noofparts = parts;
			this.no = no;
			this.fileName = fileName;

			if (this.no == 1) {
				System.out.println("Client 4 Connected");

				try {
					int partCounter = 1;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 4");
						partCounter = partCounter + 5;
					}

					partCounter = 2;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 4");
						partCounter = partCounter + 5;
					}

					partCounter = 3;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 4");
						partCounter = partCounter + 5;
					}

				} catch (IOException ex) {
					System.out.println("Error, Client3 cannot send ");
				}

				try {
					connection.close();
					System.out.println("Client 4 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 4");
				}

			}

			if (this.no == 2) {
				System.out.println("Client 4 Connected again");

				try {
					int partCounter = 5;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 4");
						partCounter = partCounter + 5;
					}
				} catch (IOException ex) {
					System.out.println("Error, Client3 cannot send ");
				}

				try {
					connection.close();
					System.out.println("Client 4 disconneted after session 2.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 4");
				}

			}
		}
	}

	public static int receiveChunk(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		String fileName = dis.readUTF();
		int parts = dis.readInt();
		int fileSize = dis.readInt();
		OutputStream out = new FileOutputStream(fileName);
		byte[] buffer = new byte[1024 * 100];
		int count;
		while (fileSize > 0 && (count = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
			out.write(buffer, 0, count);
			fileSize -= count;
		}
		System.out.println("Received Chunk: " + fileName);
		out.close();
		Name = fileName;
		return parts;
	}

	public static void sendChunk(File file, OutputStream out, int noparts) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeUTF(file.getName());
		dos.writeInt(noparts);
		dos.writeInt((int) file.length());
		int count;
		byte[] buffer = new byte[1024 * 100];
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		while ((count = in.read(buffer)) >= 0) {
			out.write(buffer, 0, count);
			out.flush();
		}
		in.close();
	}

	public static List<File> listOfFilesToMerge(File oneOfFiles) throws Exception {
		String tmpName = oneOfFiles.getName();
		File[] files = oneOfFiles.getParentFile()
				.listFiles((File dir, String name) -> name.matches(tmpName + "[.]\\d+"));
		Arrays.sort(files);
		return Arrays.asList(files);
	}

	public static void mergeFiles(List<File> files, File into) throws IOException {
		try (BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(into))) {
			for (File f : files) {
				Files.copy(f.toPath(), mergingStream);
			}
		}
	}

}
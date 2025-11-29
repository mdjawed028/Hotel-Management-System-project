import java.sql.DriverManager;
import java.sql.SQLException;
import java .sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelManagement {
    private static final String url="jdbc:mysql://localhost:3306/hotel_db";
        private static final String user="root";
        private static final String password="Jawed@91997#";

        public static void main(String[] args) throws ClassNotFoundException, SQLException {

            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            catch(ClassNotFoundException e){
                System.out.println(e.getMessage());
            }
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                while(true){
                    System.out.println();
                    System.out.println("HOTEL MANAGEMENT SYSTEM");
                    Scanner scanner =new Scanner(System.in);

                    System.out.println("1. Reserver a room");
                    System.out.println("2. view Reservations");
                    System.out.println("3. Get Room Number");
                    System.out.println("4. Update Reservation");
                    System.out.println("5. Delete Reservation");
                    System.out.println("0. Exit");
                    System.out.print("choose an option: ");
                    int choice=scanner.nextInt();

                    switch(choice){
                        case 1:
                            reserveRoom(connection,scanner);
                            break;
                        case 2:
                            viewReservations(connection);
                            break;
                        case 3:
                            getRoomNumber(connection,scanner);
                            break;
                        case 4:
                            updateReservation(connection,scanner);
                            break;
                        case 5:
                            deleteReservation(connection,scanner);
                            break;
                        case 0:
                            exit();
                            scanner.close();
                            return;
                        default:
                            System.out.println("Invalid choice.Try again.");
                    }
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            catch(InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        private static void reserveRoom(Connection connection,Scanner scanner) {
            try{
                System.out.print("Enter guest name: ");
                String guestName=scanner.next();
                scanner.nextLine();
                System.out.print("Enter room number: ");
                int roomNumber=scanner.nextInt();
                System.out.print("Enter contact number: ");
                String contactNumber=scanner.next();

                String sql="INSERT INTO reservations(guest_name,room_number,contact_number)"+
                        "VALUES ('" + guestName + "',"+ roomNumber + ", '" + contactNumber + "')";

                try (Statement statement= connection.createStatement()){
                    int affectedRows= statement.executeUpdate(sql);

                    if(affectedRows>0){
                        System.out.println("Reservation successful");
                    }
                    else{
                        System.out.println("Reservation failed");
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        private static void viewReservations(Connection connection) throws SQLException{
            String sql= "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

            try (Statement statement= connection.createStatement();
                 ResultSet resultSet= statement.executeQuery(sql)){

                System.out.println("Current Reservations:");
                System.out.println("+----------------+----------------+---------------+----------------+-------------------+");
                System.out.println("| Reservation ID |  Guest Name    | Room Number    | Contact Number | Reservation date |");
                System.out.println("+----------------+----------------+----------------+----------------+------------------+");

                while(resultSet.next()){
                    int reservationID=resultSet.getInt("reservation_id");
                    String guestName=resultSet.getString("guest_name");
                    int roomNumber=resultSet.getInt("room_number");
                    String contactNumber=resultSet.getString("contact_number");
                    String reservationDate=resultSet.getTimestamp("reservation_date").toString();

                    // Format and display the reservation date in a table like format
                    System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s |\n",
                            reservationID,guestName,roomNumber,contactNumber,reservationDate);
                }
                System.out.println("+----------------+-------------+-------------+----------------+------------------+");
            }
        }
        private static void getRoomNumber(Connection connection,Scanner scanner){
            try{
                System.out.print("Enter reservation ID: ");
                int reservationID=scanner.nextInt();
                System.out.print("Enter guest Name: ");
                String guestName=scanner.next();

                String sql="SELECT room_number FROM reservations "+
                        "WHERE reservation_id= " + reservationID +
                        " AND guest_name= '" + guestName + "'";

                try (Statement statement= connection.createStatement();
                     ResultSet resultSet= statement.executeQuery(sql)){

                    if(resultSet.next()){
                        int roomNumber= resultSet.getInt("room_number");
                        System.out.println("Room Number for Reservation ID " + reservationID +
                                " and Guest " + guestName + " is: " + roomNumber);
                    }
                    else {
                        System.out.println("reservation not found for the given ID and guest name.");
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        private static void updateReservation(Connection connection, Scanner scanner){
            try{
                System.out.print("Enter reservation ID to update: ");
                int reservationID = scanner.nextInt();
                scanner.nextLine();   // Clear buffer

                if(!reservationExists(connection, reservationID)){
                    System.out.println("Reservation not found for the given ID.");
                    return;
                }

                System.out.print("Enter new guest name: ");
                String newGuestName = scanner.nextLine();

                System.out.print("Enter new room number: ");
                int newRoomNumber = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter new contact number: ");
                String newContactNumber = scanner.nextLine();

                String sql = "UPDATE reservations SET " +
                        "guest_name = '" + newGuestName + "', " +
                        "room_number = " + newRoomNumber + ", " +
                        "contact_number = '" + newContactNumber + "' " +
                        "WHERE reservation_id = " + reservationID;

                try (Statement statement = connection.createStatement()) {
                    int affectedRows = statement.executeUpdate(sql);

                    if (affectedRows > 0) {
                        System.out.println("Reservation updated successfully");
                    } else {
                        System.out.println("Reservation update failed");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        private static void deleteReservation(Connection connection, Scanner scanner) {
            try {
                System.out.print("Enter reservation ID to delete: ");
                int reservationID = scanner.nextInt();

                if (!reservationExists(connection, reservationID)) {
                    System.out.println("Reservation not found for the given ID.");
                    return;
                }

                // FIX: Added FROM
                String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationID;

                try (Statement statement = connection.createStatement()) {
                    int affectedRows = statement.executeUpdate(sql);

                    if (affectedRows > 0) {
                        System.out.println("Reservation deleted successfully");
                    } else {
                        System.out.println("Reservation delete failed.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static boolean reservationExists(Connection connection,int reservationID){

            try{
                String sql="SELECT reservation_id FROM reservations WHERE reservation_id= " + reservationID;

                try (Statement statement= connection.createStatement();
                     ResultSet resultSet= statement.executeQuery(sql)){

                    return resultSet.next();
                }
            }
            catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }

        public static void exit() throws InterruptedException{
            System.out.print("Exiting system");
            int i=5;
            while(i!=0){
                System.out.print(".");
                Thread.sleep(450);
                i--;
            }
            System.out.println();
            System.out.println("Thank you for using Hotel Reservation System.");
        }
    }




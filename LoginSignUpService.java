public interface LoginSignUpService {
    public String authorizeLogin(String username, String password);

    public User createNewUser(String username, String password, String email, String firstName, String lastName, 
                              String profileImage) throws BadDataException;
}

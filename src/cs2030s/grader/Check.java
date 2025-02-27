package cs2030s.grader;

class Check {
    private boolean pass;
    private String error;

    public Check(boolean pass, String error) {
        this.pass = pass;
        this.error = error;
    }

    public boolean passed() {
        return pass;
    }

    public String getMessage() {
        String msg = pass ? "Check passed" : error;
        return msg;
    }
}

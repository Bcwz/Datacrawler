public class ResultSentiment  {

    private String line;
    private String cssClass;

    public ResultSentiment(String line, String cssClass) {
        super();
        this.line = line;
        this.cssClass = cssClass;
    }


    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return line + " [Sentiment: " + cssClass + "]";
    }
}
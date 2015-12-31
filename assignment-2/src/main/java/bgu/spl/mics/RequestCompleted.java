package bgu.spl.mics;

/**
 * A message indicating a completion of a request and its result.
 */
public class RequestCompleted<T> implements Message {

    private Request<T> completed;
    private T result;

    /**
	 * @param completed - the completed request
	 * @param amount - the result of the completed request
	 */
    public RequestCompleted(Request<T> completed, T result) {
        this.completed = completed;
        this.result = result;
    }

    /**
	 * @return The completed request.
	 */

    public Request<?> getCompletedRequest() {
        return completed;
    }

    /**
	 * @return The completed request result.
	 */

    public T getResult() {
        return result;
    }

}

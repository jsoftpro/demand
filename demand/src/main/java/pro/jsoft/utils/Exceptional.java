package pro.jsoft.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Exceptional<T, X extends Throwable> {
	private static final Exceptional<?, ?> EMPTY = new Exceptional<>();
	private final T value;
	private final X exception;

	private Exceptional() {
		this.value = null;
        this.exception = null;
	}

	private Exceptional(T value) {
        this.value = Objects.requireNonNull(value);
        this.exception = null;
    }

	private Exceptional(X exception) {
        this.value = null;
        this.exception = Objects.requireNonNull(exception);
    }

	public static<T, X extends Throwable> Exceptional<T, X> empty() {
        @SuppressWarnings("unchecked")
		Exceptional<T, X> e = (Exceptional<T, X>) EMPTY;
		return e;
	}

    public static <T, X extends Throwable> Exceptional<T, X> of(T value, X exception) {
    	if (exception != null) { 
            return new Exceptional<>(exception);
    	}
        return new Exceptional<>(value);
    }

    public static <T, X extends Throwable> Exceptional<T, X> ofNullable(T value, X exception) {
        return value == null && exception == null ? empty() : of(value, exception);
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean hasException() {
        return exception != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            consumer.accept(value);
    }
    
    public<U> Exceptional<U, X> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Exceptional.ofNullable(mapper.apply(value), exception);
        }
    }

    public T orElseThrow() throws X {
        if (value != null) {
            return value;
        } else if (exception != null) {
            throw exception;
        }

        throw new NoSuchElementException("No value present");
    }

    public <E extends Throwable> T orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }
}

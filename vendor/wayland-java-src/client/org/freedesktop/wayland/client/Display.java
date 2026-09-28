/*
 * Copyright © 2015 Erik De Rijcke
 * Copyright © 2024 Casey Link
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.freedesktop.wayland.client;

import org.freedesktop.wayland.raw.C;
import org.freedesktop.wayland.raw.LibWayland;
import org.freedesktop.wayland.util.Memory;

import java.lang.foreign.MemorySegment;

/**
 * Represents a connection to the compositor and acts as a proxy to
 * the {@code Display} singleton object.
 * <p>
 * A {@code Display} object represents a client connection to a Wayland
 * compositor. It is created with either {@code WlDisplayProxy.connect()} or
 * {@code WlDisplayProxy.connectToFd()}. A connection is terminated using
 * {@link #disconnect()}.
 * <p>
 * A {@code Display} is also used as the {@link Proxy} for the {@code Display}
 * singleton object on the compositor side.
 * <p>
 * A {@code Display} object handles all the data sent from and to the
 * compositor. When a {@link Proxy} marshals a request, it will write its wire
 * representation to the display's write buffer. The data is sent to the
 * compositor when the client calls {@link #flush()}.
 * <p>
 * Incoming data is handled in two steps: queueing and dispatching. In the
 * queue step, the data coming from the display fd is interpreted and
 * added to a queue. On the dispatch step, the handler for the incoming
 * event set by the client on the corresponding {@link Proxy} is called.
 * <p>
 * A {@code Display} has at least one event queue, called the <em>main
 * queue</em>. Clients can create additional event queues with \ref
 * {@link #createQueue()} and assign {@link Proxy}'s to it. Events
 * occurring in a particular proxy are always queued in its assigned queue.
 * A client can ensure that a certain assumption, such as holding a lock
 * or running from a given thread, is true when a proxy event handler is
 * called by assigning that proxy to an event queue and making sure that
 * this queue is only dispatched when the assumption holds.
 * <p>
 * The main queue is dispatched by calling {@link #dispatch()}.
 * This will dispatch any events queued on the main queue and attempt
 * to read from the display fd if its empty. Events read are then queued
 * on the appropriate queues according to the proxy assignment. Calling
 * that function makes the calling thread the <em>main thread</em>.
 * <p>
 * A user created queue is dispatched with {@link #dispatchQueue(EventQueue)}.
 * If there are no events to dispatch this function will block. If this
 * is called by the main thread, this will attempt to read data from the
 * display fd and queue any events on the appropriate queues. If calling
 * from any other thread, the function will block until the main thread
 * queues an event on the queue being dispatched.
 * <p>
 * A real world example of event queue usage is Mesa's implementation of
 * {@code eglSwapBuffers()} for the Wayland platform. This function might need
 * to block until a frame callback is received, but dispatching the main
 * queue could cause an event handler on the client to start drawing
 * again. This problem is solved using another event queue, so that only
 * the events handled by the EGL code are dispatched during the block.
 * <p>
 * This creates a problem where the main thread dispatches a non-main
 * queue, reading all the data from the display fd. If the application
 * would call <em> poll(2)</em> after that it would block, even though there
 * might be events queued on the main queue. Those events should be
 * dispatched with {@link #dispatchPending()} before
 * flushing and blocking.
 */
public abstract class Display extends Proxy<Void> {

    public static final int OBJECT_ID = 1;

    protected static MemorySegment _connect(String name) {
        var ptr = LibWayland.wl_display_connect(Memory.ARENA_AUTO.allocateFrom(name));
        if (MemorySegment.NULL.equals(ptr))
            throw new RuntimeException("Unable to connect to display with name " + name);
        return ptr;
    }

    protected static MemorySegment _connect(int fd) {
        var ptr = LibWayland.wl_display_connect_to_fd(fd);
        if (MemorySegment.NULL.equals(ptr))
            throw new RuntimeException("Unable to connect to display with file descriptor " + fd);
        return ptr;
    }

    public static MemorySegment _connect() {
        var ptr = LibWayland.wl_display_connect(MemorySegment.NULL);
        if (MemorySegment.NULL.equals(ptr))
            throw new RuntimeException("Unable to auto connect to a display");
        return ptr;
    }


    protected Display(final MemorySegment pointer) {
        super(pointer,
                null,
                1);
    }

    /**
     * Close a connection to a Wayland display
     * <p>
     * Close the connection to the display and free all resources associated
     * with it.
     */
    public void disconnect() {
        LibWayland.wl_display_disconnect(this.pointer);
    }

    /**
     * Get a display context's file descriptor
     * <p>
     * Return the file descriptor associated with a display so it can be
     * integrated into the client's main loop.
     *
     * @return Display object file descriptor
     */
    public int getFD() {
        return LibWayland.wl_display_get_fd(this.pointer);
    }

    /**
     * Process incoming events
     * <p>
     * Dispatch the display's main event queue.
     * <p>
     * If the main event queue is empty, this function blocks until there are
     * events to be read from the display fd. Events are read and queued on
     * the appropriate event queues. Finally, events on the main event queue
     * are dispatched.
     * <p>
     * It is not possible to check if there are events on the main queue
     * or not. For dispatching main queue events without blocking, see {@link #dispatchPending()}.
     * <p>
     * Calling this will release the display file descriptor if this
     * thread acquired it using wl_display_acquire_fd().
     *
     * @return The number of dispatched events on success or -1 on failure
     * @see #dispatchPending()
     * @see #dispatchQueue(EventQueue)
     */
    public int dispatch() {
        return LibWayland.wl_display_dispatch(this.pointer);
    }

    /**
     * Dispatch main queue events without reading from the display fd
     * <p>
     * This function dispatches events on the main event queue. It does not
     * attempt to read the display fd and simply returns zero if the main
     * queue is empty, i.e., it doesn't block.
     * <p>
     * This is necessary when a client's main loop wakes up on some fd other
     * than the display fd (network socket, timer fd, etc) and calls
     * {@link #dispatchQueue(EventQueue)} from that callback. This may queue up
     * events in the main queue while reading all data from the display fd.
     * When the main thread returns to the main loop to block, the display fd
     * no longer has data, causing a call to {@code poll(2)} (or similar
     * functions) to block indefinitely, even though there are events ready
     * to dispatch.
     * <p>
     * To proper integrate the wayland display fd into a main loop, the
     * client should always call {@code dispatchPending()} and then
     * {@link #flush()} prior to going back to sleep. At that point,
     * the fd typically doesn't have data so attempting I/O could block, but
     * events queued up on the main queue should be dispatched.
     * <p>
     * A real-world example is a main loop that wakes up on a timerfd (or a
     * sound card fd becoming writable, for example in a video player), which
     * then triggers GL rendering and eventually eglSwapBuffers().
     * eglSwapBuffers() may call {@link #dispatchQueue(EventQueue)} if it didn't
     * receive the frame event for the previous frame, and as such queue
     * events in the main queue.
     * <p>
     * Calling this makes the current thread the main one.
     *
     * @return The number of dispatched events or -1 on failure
     * @see #dispatch()
     * @see #dispatchQueue(EventQueue)
     * @see #flush()
     */
    public int dispatchPending() {
        return LibWayland.wl_display_dispatch_pending(this.pointer);
    }

    /**
     * Dispatch events in an event queue
     * <p>
     * Dispatch all incoming events for objects assigned to the given
     * event queue. On failure -1 is returned and errno set appropriately.
     * <p>
     * This function blocks if there are no events to dispatch. If calling from
     * the main thread, it will block reading data from the display fd. For other
     * threads this will block until the main thread queues events on the queue
     * passed as argument.
     *
     * @param queue The event queue to dispatch
     * @return The number of dispatched events on success or -1 on failure
     */
    public int dispatchQueue(final EventQueue queue) {
        return LibWayland.wl_display_dispatch_queue(this.pointer,
                queue.pointer);
    }

    /**
     * Dispatch pending events in an event queue
     * <p>
     * Dispatch all incoming events for objects assigned to the given
     * event queue. On failure -1 is returned and errno set appropriately.
     * If there are no events queued, this function returns immediately.
     *
     * @param queue The event queue to dispatch
     * @return The number of dispatched events on success or -1 on failure
     * @since 1.0.2
     */
    public int dispatchQueuePending(final EventQueue queue) {
        return LibWayland.wl_display_dispatch_queue_pending(this.pointer,
                queue.pointer);
    }

    /**
     * Send all buffered requests on the display to the server
     * <p>
     * Send all buffered data on the client side to the server. Clients
     * should call this function before blocking. On success, the number
     * of bytes sent to the server is returned. On failure, this
     * function returns -1 and errno is set appropriately.
     * <p>
     * flush() never blocks.  It will write as much data as
     * possible, but if all data could not be written, errno will be set
     * to EAGAIN and -1 returned.  In that case, use poll on the display
     * file descriptor to wait for it to become writable again.
     *
     * @return The number of bytes sent on success or -1 on failure
     */
    public int flush() {
        return LibWayland.wl_display_flush(this.pointer);
    }

    /**
     * Block until all pending request are processed by the server
     * <p>
     * Blocks until the server process all currently issued requests and
     * sends out pending events on all event queues.
     *
     * @return The number of dispatched events on success or -1 on failure
     */
    public int roundtrip() {
        return LibWayland.wl_display_roundtrip(this.pointer);
    }

    /**
     * Create a new event queue for this display
     * <p>
     *
     * @return A new event queue associated with this display or NULL on
     * failure.
     */
    public EventQueue createQueue() {
        return new EventQueue(LibWayland.wl_display_create_queue(this.pointer));
    }

    /**
     * Retrieve the last error that occurred on a display
     * <p>
     * Return the last error that occurred on the display. This may be an error sent
     * by the server or caused by the local client.
     * <p>
     * Errors are <b>fatal</b>. If this function returns non-zero the display
     * can no longer be used.
     *
     * @return The last error that occurred on display or 0 if no error occurred
     */
    public int getError() {
        return LibWayland.wl_display_get_error(this.pointer);
    }

    public int prepareReadQueue(final EventQueue queue) {
        return LibWayland.wl_display_prepare_read_queue(this.pointer, queue.pointer);
    }

    /**
     * Prepare to read events after polling file descriptor
     * <p>
     * This function must be called before reading from the file
     * descriptor using {@link #readEvents()}.  Calling
     * {@code prepareRead()} announces the calling threads intention
     * to read and ensures that until the thread is ready to read and
     * calls {@link #readEvents()}, no other thread will read from the
     * file descriptor.  This only succeeds if the event queue is empty
     * though, and if there are undispatched events in the queue, -1 is
     * returned and errno set to EAGAIN.
     * <p>
     * If a thread successfully calls {@code prepareRead()}, it must
     * either call {@link #readEvents()} when it's ready or cancel the
     * read intention by calling {@link #cancelRead()}.
     * <p>
     * Use this function before polling on the display fd or to integrate
     * the fd into a toolkit event loop in a race-free way.  Typically, a
     * toolkit will call {@link #dispatchPending()} before sleeping, to
     * make sure it doesn't block with unhandled events.  Upon waking up,
     * it will assume the file descriptor is readable and read events from
     * the fd by calling {@link #dispatch()}.  Simplified, we have:
     * <p>
     * <pre>
     *   {@code
     *
     *   display.dispatchPending();
     *   display.flush();
     *   poll(fds, nfds, -1);
     *   display.dispatch();
     *   }
     * </pre>
     * <p>
     * There are two races here: first, before blocking in poll(), the fd
     * could become readable and another thread reads the events.  Some of
     * these events may be for the main queue and the other thread will
     * queue them there and then the main thread will go to sleep in
     * poll().  This will stall the application, which could be waiting
     * for a event to kick of the next animation frame, for example.
     * <p>
     * The other race is immediately after poll(), where another thread
     * could preempt and read events before the main thread calls
     * {@link #dispatch()}.  This call now blocks and starves the other
     * fds in the event loop.
     * <p>
     * A correct sequence would be:
     * <p>
     * <pre>
     *   {@code
     *   while (display.prepareRead() != 0)
     *           display.dispatch_Pending();
     *   display.flush();
     *   poll(fds, nfds, -1);
     *   display.readEvents();
     *   display.dispatchPending();
     *   }
     * </pre>
     * <p>
     * Here we call {@code prepareRead()}, which ensures that between
     * returning from that call and eventually calling
     * {@link #readEvents()}, no other thread will read from the fd and
     * queue events in our queue.  If the call to
     * {@code prepareRead()} fails, we dispatch the pending events and
     * try again until we're successful.
     *
     * @return 0 on success or -1 if event queue was not empty
     */
    public int prepareRead() {
        return LibWayland.wl_display_prepare_read(this.pointer);
    }

    /**
     * Release exclusive access to display file descriptor
     * <p>
     * This releases the exclusive access.  Useful for canceling the lock
     * when a timed out poll returns fd not readable and we're not going
     * to read from the fd anytime soon.
     */
    public void cancelRead() {
        LibWayland.wl_display_cancel_read(this.pointer);
    }

    /**
     * Read events from display file descriptor
     * <p>
     * This will read events from the file descriptor for the display.
     * This function does not dispatch events, it only reads and queues
     * events into their corresponding event queues.  If no data is
     * avilable on the file descriptor, {@code readEvents()} returns
     * immediately.  To dispatch events that may have been queued, call
     * {@link #dispatchPending()} or
     * {@link #dispatchQueuePending(EventQueue)}.
     * <p>
     * Before calling this function, {@link #prepareRead()} must be
     * called first.
     *
     * @return 0 on success or -1 on error.  In case of error errno will
     * be set accordingly
     */
    public int readEvents() {
        return LibWayland.wl_display_read_events(this.pointer);
    }
}


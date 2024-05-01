public interface PipeLike {

    public boolean canPull();
    public boolean canPush();

    public void flush(int fromRobEntry);

    public void push(PipelineEntry e);

    public PipelineEntry pull();

    public PipelineEntry peek();

    public int getCount();

    public boolean isEmpty();
}

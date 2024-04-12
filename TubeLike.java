public interface TubeLike {

    public boolean canPull();
    public boolean canPush();

    public void flush();

    public void push(PipelineEntry e);

    public PipelineEntry pull();

    public PipelineEntry peek();
}

public class FetchUnit {

    private final PipelineRegister last;
    private final PipelineRegister next;

    FetchUnit(PipelineRegister last, PipelineRegister next){
        this.last = last;
        this.next = next;
    }

}

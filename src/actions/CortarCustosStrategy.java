package actions;
public class CortarCustosStrategy implements DecisaoStrategy {
    @Override
    public model.Deltas aplicar(model.Startup s){
        return new model.Deltas(8_000, 0, -5, 0.0); //+8k caixa, -5 moral
    } 
}
package actions;
public class EquipeStrategy implements DecisaoStrategy {
    @Override
    public model.Deltas aplicar(model.Startup s){
         return new model.Deltas(-5_000, 0, 7, 0.0);//-5k caixa, +7 rep
        } 
    }
package actions;
public class MarketingStrategy implements DecisaoStrategy { 
    @Override
    public model.Deltas aplicar(model.Startup s){
       return new model.Deltas(-10_000, 5, 0, 0.03);//-10k caixa, +5 rep, +3% receita
     } }
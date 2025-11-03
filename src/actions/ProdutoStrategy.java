package actions;
public class ProdutoStrategy implements DecisaoStrategy {
    @Override
    public model.Deltas aplicar(model.Startup s){
       return new model.Deltas(-8_000, 0, 0, 0.04);//-8k caixa, +4% receita
        } }
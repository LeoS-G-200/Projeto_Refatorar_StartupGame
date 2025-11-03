package actions; 
public class InvestidoresStrategy implements DecisaoStrategy {
    @Override
    public model.Deltas aplicar(model.Startup s){
         boolean aprovado = Math.random() < 0.60; //60% chance
         if (aprovado) {
             return new model.Deltas(40_000, 0, 0, 0.0); //+40k caixa, +3rep
         } else {
             return new model.Deltas(0, -2, 0, 0.0); //-2 rep
         }
        } 
    }
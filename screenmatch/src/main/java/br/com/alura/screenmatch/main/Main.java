package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverterDados;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverterDados conversor = new ConverterDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=e8addb47";
    private List<DadosSerie> dadosSeries = new ArrayList<>();


    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBuscada;

    public Main(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar Series buscadas
                4 - Buscar Serie por Titulo
                5 - Buscar Series por Ator
                6 - Top 5 Series
                7 - Buscar Series por Categoria
                8 - Filtrar Series
                9 - Buscar episodio por trecho
                10 - Mostrar os 5 melhores episodios de uma serie
                11 -  Buscar episodios a partir de uma data
                
                0 - Sair                                 
                """;

        System.out.println(menu);
        opcao = leitura.nextInt();
        leitura.nextLine();

        switch (opcao) {
            case 1:
                buscarSerieWeb();
                break;
            case 2:
                buscarEpisodioPorSerie();
                break;

            case 3:
                listarSeriesBuscadas();
                break;

            case 4:
                buscarSeriePorTitulo();
                break;

            case 5:
                buscarSeriesPorAtor();
                break;

            case 6:
                buscarTop5Series();
                break;

            case 7:
            buscarSeriePorCategoria();
            break;

            case 8:
            buscarSeriesPorQuantidadeTemporadasEAvaliacao();
            break;

            case 9:
                buscarEpisodioPorTrecho();
                break;

            case 10:
                topEpisodiosPorSerie();
                break;

            case 11:
                buscarEpisodiosPorData();
                break;


            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
        }
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
         serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("dados da serie: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (serie.isPresent()) {

        var serieEncontrada = serie.get();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream().flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e))).collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
        System.out.println("Série não encontrada");
    }
    }


    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator para busca");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliacão minima: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Series em que " + nomeAtor + " atuou: ");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> top5Series = repositorio.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("Top 5 Series: ");
        top5Series.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Digite o nome da categoria para busca");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da categoria " + nomeGenero + ": ");
        seriesPorCategoria.forEach(System.out::println);

    }

    private void buscarSeriesPorQuantidadeTemporadasEAvaliacao(){
        System.out.println("Digite a quantidade de temporadas para busca");
        var quantidadeTemporadas = leitura.nextInt();
        System.out.println("Digite a avaliação minima para busca");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesPorQuantidadeTemporadasEAvaliacao = repositorio.seriesPorTemporadaEAvaliacao(quantidadeTemporadas, avaliacao);
        System.out.println("Series com " + quantidadeTemporadas + " temporadas e avaliação maior ou igual a " + avaliacao + ": ");
        seriesPorQuantidadeTemporadasEAvaliacao.forEach(System.out::println);
    }



    private void buscarEpisodioPorTrecho(){
        System.out.println("Qual o nome do episódio para busca?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTempoorada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }
    private void topEpisodiosPorSerie(){
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodio = repositorio.topEpisodiosPorSerie(serie);
            topEpisodio.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliacao %s\n",
                            e.getSerie().getTitulo(), e.getTempoorada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }

    }

    private void buscarEpisodiosPorData(){
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            System.out.println("Digite o ano limite para busca");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie,anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

}



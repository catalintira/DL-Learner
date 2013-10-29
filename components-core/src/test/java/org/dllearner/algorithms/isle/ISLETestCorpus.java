/**
 * 
 */
package org.dllearner.algorithms.isle;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.dllearner.algorithms.celoe.CELOE;
import org.dllearner.algorithms.isle.index.*;
import org.dllearner.algorithms.isle.index.semantic.SemanticIndex;
import org.dllearner.algorithms.isle.index.semantic.simple.SimpleSemanticIndex;
import org.dllearner.algorithms.isle.index.syntactic.OWLOntologyLuceneSyntacticIndexCreator;
import org.dllearner.algorithms.isle.index.syntactic.SyntacticIndex;
import org.dllearner.algorithms.isle.metrics.PMIRelevanceMetric;
import org.dllearner.algorithms.isle.metrics.RelevanceMetric;
import org.dllearner.algorithms.isle.metrics.RelevanceUtils;
import org.dllearner.algorithms.isle.textretrieval.EntityTextRetriever;
import org.dllearner.algorithms.isle.textretrieval.RDFSLabelEntityTextRetriever;
import org.dllearner.algorithms.isle.wsd.SimpleWordSenseDisambiguation;
import org.dllearner.algorithms.isle.wsd.WordSenseDisambiguation;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Entity;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.kb.OWLAPIOntology;
import org.dllearner.learningproblems.ClassLearningProblem;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.utilities.Helper;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Some tests for the ISLE algorithm.
 * 
 * @author Lorenz Buehmann
 * @author Jens Lehmann
 */
public class ISLETestCorpus {
	
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory df = new OWLDataFactoryImpl();
	private EntityTextRetriever textRetriever;
	private RelevanceMetric relevance;
	private String searchField = "label";
	private SemanticIndex semanticIndex;
	private SyntacticIndex syntacticIndex;
	
	// we assume that the ontology is named "ontology.owl" and that all text files
	// are in a subdirectory called "corpus"
	private String testFolder = "../test/isle/swore/";
	private NamedClass cls = new NamedClass("http://ns.softwiki.de/req/CustomerRequirement");
//	private String testFolder = "../test/isle/father/";
//	private NamedClass cls = new NamedClass("http://example.com/father#father");

	
	/**
	 * 
	 */
	public ISLETestCorpus() throws Exception{
		manager = OWLManager.createOWLOntologyManager();
		ontology = manager.loadOntologyFromOntologyDocument(new File(testFolder + "ontology.owl"));
		textRetriever = new RDFSLabelEntityTextRetriever(ontology);
		syntacticIndex = new OWLOntologyLuceneSyntacticIndexCreator(ontology, df.getRDFSLabel(), searchField).buildIndex();
		
		
	}
	
	private Set<TextDocument> createDocuments(){
		Set<TextDocument> documents = new HashSet<TextDocument>();
		File folder = new File(testFolder+"corpus/");
		for (File file  : folder.listFiles()) {
			if(!file.isDirectory() && !file.isHidden()){
				try {
					String text = Files.toString(file, Charsets.UTF_8);
					documents.add(new TextDocument(text));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return documents;
	}
	
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception{
		
	}

//	@Test
	public void testTextRetrieval() {
		System.out.println("Text for entity " + cls + ":");
		Map<String, Double> relevantText = textRetriever.getRelevantText(cls);
		System.out.println(Joiner.on("\n").join(relevantText.entrySet()));
	}
	
//	@Test
	public void testEntityRelevance() throws Exception {
		System.out.println("Relevant entities for entity " + cls + ":");
		Map<Entity, Double> entityRelevance = RelevanceUtils.getRelevantEntities(cls, ontology, relevance);
		System.out.println(Joiner.on("\n").join(entityRelevance.entrySet()));
	}
	
	@Test
	public void testSemanticIndexAnnotationProperty(){
		semanticIndex = new SimpleSemanticIndex(ontology, syntacticIndex);
		semanticIndex.buildIndex(df.getRDFSLabel(), null);		
//		NamedClass nc = new NamedClass("http://example.com/father#father");
		Set<AnnotatedDocument> documents = semanticIndex.getDocuments(cls);
		System.out.println("Documents for " + cls + ":\n" + documents);
	}
	
	@Test
	public void testSemanticIndexCorpus(){
		semanticIndex = new SimpleSemanticIndex(ontology, syntacticIndex);
		semanticIndex.buildIndex(createDocuments());
		Set<AnnotatedDocument> documents = semanticIndex.getDocuments(cls);
		System.out.println(documents);
	}
	
	@Test
	public void testISLE() throws Exception {
		KnowledgeSource ks = new OWLAPIOntology(ontology);
		AbstractReasonerComponent reasoner = new FastInstanceChecker(ks);
		reasoner.init();
		
		ClassLearningProblem lp = new ClassLearningProblem(reasoner);
		lp.setClassToDescribe(cls);
		lp.init();
		
		semanticIndex = new SimpleSemanticIndex(ontology, syntacticIndex);
		semanticIndex.buildIndex(createDocuments());
		
		relevance = new PMIRelevanceMetric(semanticIndex);
		
		Map<Entity, Double> entityRelevance = RelevanceUtils.getRelevantEntities(cls, ontology, relevance);
		NLPHeuristic heuristic = new NLPHeuristic(entityRelevance);
		
		ISLE isle = new ISLE(lp, reasoner);
		isle.setHeuristic(heuristic);
		isle.init();
		
		isle.start();
	}
	
    @Test
    public void testEntityLinkingWithLemmatizing() throws Exception {
        EntityCandidatesTrie ect = new SimpleEntityCandidatesTrie(new RDFSLabelEntityTextRetriever(ontology), ontology,
                new SimpleEntityCandidatesTrie.LemmatizingWordNetNameGenerator(5));
        LinguisticAnnotator linguisticAnnotator = new TrieLinguisticAnnotator(ect);
        WordSenseDisambiguation wsd = new SimpleWordSenseDisambiguation(ontology);
        EntityCandidateGenerator ecg = new TrieEntityCandidateGenerator(ontology, ect);
        SemanticAnnotator semanticAnnotator = new SemanticAnnotator(wsd, ecg, linguisticAnnotator);

        Set<TextDocument> docs = createDocuments();
        for (TextDocument doc : docs) {
            AnnotatedDocument annotated = semanticAnnotator.processDocument(doc);
            System.out.println(annotated);
        }
    }

    @Test
    public void testEntityLinkingWithSimpleStringMatching() throws Exception {
        EntityCandidatesTrie ect = new SimpleEntityCandidatesTrie(new RDFSLabelEntityTextRetriever(ontology), ontology,
                new SimpleEntityCandidatesTrie.DummyNameGenerator());
        TrieLinguisticAnnotator linguisticAnnotator = new TrieLinguisticAnnotator(ect);
        linguisticAnnotator.setNormalizeWords(false);
        WordSenseDisambiguation wsd = new SimpleWordSenseDisambiguation(ontology);
        EntityCandidateGenerator ecg = new TrieEntityCandidateGenerator(ontology, ect);
        SemanticAnnotator semanticAnnotator = new SemanticAnnotator(wsd, ecg, linguisticAnnotator);

        Set<TextDocument> docs = createDocuments();
        for (TextDocument doc : docs) {
            AnnotatedDocument annotated = semanticAnnotator.processDocument(doc);
            System.out.println(annotated);
        }
    }

	@Test
	public void compareISLE() throws Exception {
		KnowledgeSource ks = new OWLAPIOntology(ontology);
		AbstractReasonerComponent reasoner = new FastInstanceChecker(ks);
		reasoner.init();
		
		ClassLearningProblem lp = new ClassLearningProblem(reasoner);
		lp.setClassToDescribe(cls);
		lp.init();
		
		semanticIndex = new SimpleSemanticIndex(ontology, syntacticIndex, false);
		semanticIndex.buildIndex(createDocuments());
		
		relevance = new PMIRelevanceMetric(semanticIndex);
		
		Map<Entity, Double> entityRelevance = RelevanceUtils.getRelevantEntities(cls, ontology, relevance);
		NLPHeuristic heuristic = new NLPHeuristic(entityRelevance);
		
		// run ISLE
		ISLE isle = new ISLE(lp, reasoner);
		isle.setHeuristic(heuristic);
		isle.setSearchTreeFile(testFolder + "searchTreeISLE.txt");
		isle.setWriteSearchTree(true);
//		isle.setReplaceSearchTree(true);
		isle.setTerminateOnNoiseReached(true);
		isle.init();
		isle.start();
		
		// run standard CELOE as reference
		CELOE celoe = new CELOE(lp, reasoner);
//		celoe.setHeuristic(heuristic);
		celoe.setSearchTreeFile(testFolder + "searchTreeCELOE.txt");
		celoe.setWriteSearchTree(true);
		celoe.setTerminateOnNoiseReached(true);
		celoe.setReplaceSearchTree(true);
		celoe.init();
		celoe.start();
		System.out.println();
		
		DecimalFormat df = new DecimalFormat("#00.00");
		System.out.println("Summary ISLE vs. CELOE");
		System.out.println("======================");
		System.out.println("accuracy:           " + df.format(100*isle.getCurrentlyBestAccuracy())+"%  vs.  " + df.format(100*celoe.getCurrentlyBestAccuracy())+"%");
		System.out.println("expressions tested: " + isle.getClassExpressionTests() + "  vs.  " + celoe.getClassExpressionTests());
		System.out.println("search tree nodes:  " + isle.getNodes().size() + "  vs.  " + celoe.getNodes().size());
		System.out.println("runtime:            " + Helper.prettyPrintNanoSeconds(isle.getTotalRuntimeNs()) + "  vs.  " + Helper.prettyPrintNanoSeconds(celoe.getTotalRuntimeNs()));
	
		// only ISLE
//		System.out.println("accuracy:           " + df.format(100*isle.getCurrentlyBestAccuracy())+"%");
//		System.out.println("expressions tested: " + isle.getClassExpressionTests());
//		System.out.println("search tree nodes:  " + isle.getNodes().size());
//		System.out.println("runtime:            " + Helper.prettyPrintNanoSeconds(isle.getTotalRuntimeNs()));
		
	}	
	
	@Test
	public void testWordSenseDisambiguation() throws Exception {
		Set<OWLEntity> context = StructuralEntityContext.getContext(ontology, df.getOWLClass(IRI.create(cls.getName())));
		System.out.println(context);
		
		Set<String> contextNL = StructuralEntityContext.getContextInNaturalLanguage(ontology, df.getOWLClass(IRI.create(cls.getName())));
		System.out.println(contextNL);
	}
	
	
}
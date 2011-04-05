/**
 * Copyright (C) 2007-2011, Jens Lehmann
 *
 * This file is part of DL-Learner.
 *
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.dllearner.scripts.improveWikipedia;

import org.aksw.commons.sparql.core.ResultSetRenderer;
import org.aksw.commons.sparql.core.SparqlEndpoint;
import org.aksw.commons.sparql.core.SparqlTemplate;
import org.aksw.commons.sparql.core.decorator.CachingSparqlEndpoint;
import org.aksw.commons.sparql.core.impl.HttpSparqlEndpoint;
import org.apache.velocity.VelocityContext;
import org.dllearner.algorithms.celoe.CELOE;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.OntologyFormat;
import org.dllearner.core.ReasonerComponent;
import org.dllearner.core.configurators.CELOEConfigurator;
import org.dllearner.core.owl.*;
import org.dllearner.gui.Config;
import org.dllearner.gui.ConfigSave;
import org.dllearner.kb.sparql.SparqlKnowledgeSource;
import org.dllearner.learningproblems.PosNegLPStandard;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.utilities.Helper;
import org.dllearner.utilities.datastructures.Datastructures;
import org.dllearner.utilities.datastructures.SetManipulation;
import org.dllearner.utilities.datastructures.SortedSetTuple;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * A script, which learns definitions / super classes of classes in the DBpedia ontology.
 *
 * @author Jens Lehmann
 */
public class DBpediaClassLearnerCELOE {

    public static String endpointurl = "http://139.18.2.96:8910/sparql";

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DBpediaClassLearnerCELOE.class);

    SparqlEndpoint sparqlEndpoint = new CachingSparqlEndpoint(new HttpSparqlEndpoint(endpointurl, "http://dbpedia.org"), "cache/");

    public DBpediaClassLearnerCELOE() {
        // OPTIONAL: if you want to do some case distinctions in the learnClass method, you could add
        // parameters to the constructure e.g. YAGO_
    }

    public KB learnAllClasses(Set<String> classesToLearn) {
        KB kb = new KB();
        for (String classToLearn : classesToLearn) {
            try {
                Description d = learnClass(classToLearn);
                if (d == null) {
                    continue;
                }
                kb.addAxiom(new EquivalentClassesAxiom(new NamedClass(classToLearn), d));
                System.out.println(d);
            } catch (Exception e) {
                logger.warn("", e);
            }
        }

        return kb;
    }

    public Description learnClass(String classToLearn) throws Exception {
        SortedSet<String> posEx = new TreeSet<String> (getPosEx(classToLearn));
        logger.info("Found " + posEx.size() + " positive examples");
        if (posEx.isEmpty()) {
            return null;
        }
        SortedSet<String> negEx =  new TreeSet<String> (getNegEx(classToLearn, posEx));

        posEx = SetManipulation.fuzzyShrink(posEx, 100);
        negEx = SetManipulation.fuzzyShrink(negEx, 100);

        SortedSet<Individual> posExamples = Helper.getIndividualSet(posEx);
        SortedSet<Individual> negExamples = Helper.getIndividualSet(negEx);
        SortedSetTuple<Individual> examples = new SortedSetTuple<Individual>(posExamples, negExamples);

        ComponentManager cm = ComponentManager.getInstance();

        SparqlKnowledgeSource ks = cm.knowledgeSource(SparqlKnowledgeSource.class);
        ks.getConfigurator().setInstances(Datastructures.individualSetToStringSet(examples.getCompleteSet()));
        //ks.getConfigurator().setPredefinedEndpoint("DBPEDIA"); // TODO: probably the official endpoint is too slow?
        ks.getConfigurator().setUrl(new URL(endpointurl));
        ks.getConfigurator().setUseLits(false);
        ks.getConfigurator().setUseCacheDatabase(true);
         ks.getConfigurator().setRecursionDepth(2);
         ks.getConfigurator().setSaveExtractedFragment(true);
         ks.getConfigurator().setPredList( new HashSet<String> (Arrays.asList(new String[]{
                 "http://dbpedia.org/property/wikiPageUsesTemplate",
                 "http://dbpedia.org/ontology/wikiPageExternalLink",
                 "http://dbpedia.org/property/wordnet_type",
         "http://www.w3.org/2002/07/owl#sameAs"})));

         ks.getConfigurator().setObjList(new HashSet<String> (Arrays.asList(new String[]{"http://dbpedia.org/class/yago/","http://dbpedia.org/resource/Category:"})));





        ks.init();

        ReasonerComponent rc = cm.reasoner(FastInstanceChecker.class, ks);
        rc.init();

        PosNegLPStandard lp = cm.learningProblem(PosNegLPStandard.class, rc);
        lp.getConfigurator().setAccuracyMethod("fMeasure");
        lp.getConfigurator().setUseApproximations(false);
        lp.init();

        CELOE la = cm.learningAlgorithm(CELOE.class, lp, rc);
        CELOEConfigurator cc = la.getConfigurator();
        cc.setMaxExecutionTimeInSeconds(100);
        cc.setUseNegation(false);
        cc.setUseAllConstructor(false);
        cc.setUseCardinalityRestrictions(false);
        cc.setUseHasValueConstructor(true);
        cc.setNoisePercentage(20);
        la.init();

        // to write the above configuration in a conf file (optional)
        Config cf = new Config(cm, ks, rc, lp, la);
        new ConfigSave(cf).saveFile(new File("/dev/null"));

        la.start();


        return la.getCurrentlyBestDescription();
    }

    public static void main(String args[]) throws LearningProblemUnsupportedException, IOException {

        DBpediaClassLearnerCELOE dcl = new DBpediaClassLearnerCELOE();
        Set<String> classesToLearn = dcl.getClasses();

        KB kb = dcl.learnAllClasses(classesToLearn);
        kb.export(new File("result.owl"), OntologyFormat.RDF_XML);
    }

    public Set<String> getClasses() {
        SparqlTemplate st = new SparqlTemplate("allClasses.vm");
        st.setLimit(0);
        st.addFilter(sparqlEndpoint.like("classes", new HashSet<String>(Arrays.asList(new String[]{"http://dbpedia.org/ontology/"}))));
        VelocityContext vc = st.getVelocityContext();
        String query = st.getQuery();
        return new HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query)));
    }

    public Set<String> getPosEx(String clazz) {
        SparqlTemplate st = new SparqlTemplate("instancesOfClass.vm");
        st.setLimit(0);
        VelocityContext vc = st.getVelocityContext();
        System.out.println(clazz);
        vc.put("class", clazz);
        String query = st.getQuery();
        return new HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query)));
    }


    public String selectClass(String clazz, Set<String> posEx) {
        Map<String, Integer> m = new HashMap<String, Integer>();

        for (String pos : posEx) {
            SparqlTemplate st = new SparqlTemplate("directClassesOfInstance.vm");
            st.setLimit(0);
            st.addFilter(sparqlEndpoint.like("classes", new HashSet<String>(Arrays.asList(new String[]{"http://dbpedia.org/ontology/"}))));
            VelocityContext vc = st.getVelocityContext();
            vc.put("instance", pos);
            String query = st.getQuery();
            Set<String> classes = new HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query)));
            classes.remove(clazz);
            for (String s : classes) {
                if (m.get(s) == null) {
                    m.put(s, 0);
                }
                m.put(s, m.get(s).intValue() + 1);
            }
        }


        int max = 0;
        String maxClass = "";
        for (String key : m.keySet()) {
            if (m.get(key).intValue() > max) {
                maxClass = key;
            }
        }
        return maxClass;
    }

    public Set<String> getNegEx(String clazz, Set<String> posEx) {
        Set<String> negEx = new HashSet<String>();
        String targetClass = getParallelClass(clazz);
        if (targetClass != null) {

            SparqlTemplate st = new SparqlTemplate("instancesOfClass.vm");
            st.setLimit(0);
            VelocityContext vc = st.getVelocityContext();
            vc.put("class", targetClass);
            st.addFilter(sparqlEndpoint.like("class", new HashSet<String>(Arrays.asList(new String[]{"http://dbpedia.org/ontology/"}))));
            String query = st.getQuery();
            negEx.addAll(new HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query))));
        } else {

            SparqlTemplate st = new SparqlTemplate("someInstances.vm");
            st.setLimit(posEx.size() + 100);
            VelocityContext vc = st.getVelocityContext();
            String query = st.getQuery();
            negEx.addAll(new HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query))));
        }
        negEx.removeAll(posEx);
        return negEx;


    }


    public String getParallelClass(String clazz) {
        SparqlTemplate st = new SparqlTemplate("parallelClass.vm");
        st.setLimit(0);
        VelocityContext vc = st.getVelocityContext();
        vc.put("class", clazz);
        String query = st.getQuery();
        Set<String> negEx = new HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query)));
        for (String s : negEx) {
            return s;
        }
        return null;
    }

}

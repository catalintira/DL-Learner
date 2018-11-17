--
-- PostgreSQL database dump
--

-- Dumped from database version 10.5 (Debian 10.5-1)
-- Dumped by pg_dump version 10.5 (Debian 10.5-1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: area_feature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.area_feature (
    iri character varying(255),
    the_geom public.geometry
);


ALTER TABLE public.area_feature OWNER TO postgres;

--
-- Name: line_feature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.line_feature (
    iri character varying(255),
    the_geom public.geometry
);


ALTER TABLE public.line_feature OWNER TO postgres;

--
-- Name: point_feature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.point_feature (
    iri character varying(255),
    the_geom public.geometry
);


ALTER TABLE public.point_feature OWNER TO postgres;

--
-- Data for Name: area_feature; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.area_feature (iri, the_geom) FROM stdin;
http://dl-learner.org/ont/spatial#building_bhf_neustadt_geometry	0106000000020000000103000000010000001900000020B8CA13087B2B40BA76B3507C884940C6F253C14D7B2B403E8C5AAB87884940727385D2727B2B40BEAB79E9818849403C4A253CA17B2B40AF44FBB37A8849408636A5619D7B2B409E4FD31D7A884940867071F9B47B2B40292BE16476884940CC54D2D4FC7A2B40A30B45155988494027333910ED7A2B403933B044568849404B51D3D3EC7A2B4069B74C3C56884940B356FEC6E87A2B40F152A005578849403198BF42E67A2B407A92CF865788494023C1AFECDD7A2B405540EB2B598849407E2C8E14DB7A2B404FF344C659884940B7291E17D57A2B40079D6BF35A88494094731882D27A2B406058FE7C5B8849401B8AF150CA7A2B40BF88111D5D88494041A08E22C67A2B406B9E23F25D8849402C144438C17A2B404161F5EC5E8849400A890F47B27A2B403F2773E2618849402CBD91D49D7A2B40A298BC016688494024134B25977A2B402AEC585B67884940681AB913917A2B4006103E9468884940BD38F1D58E7A2B4071DA9DFF6888494020B8CA13087B2B40BA76B3507C88494020B8CA13087B2B40BA76B3507C884940010300000001000000580000003DDA931BA07B2B40AE7488C965884940E2A8818BBA7B2B40C41A78FF69884940EEA4749FD27B2B4069F118D46D8849403AC6BA5DD47B2B40BC934F8F6D8849406359D537D57B2B409242B4B16D8849406FD34444D67B2B4081D7C2876D884940274F594DD77B2B409242B4B16D8849408C14CAC2D77B2B405D8B16A06D8849403FC8B260E27B2B40C7F319506F884940D4FD52F5E17B2B4068EBE0606F88494086747808E37B2B400E16A98B6F88494033068CE4E17B2B40737FF5B86F884940A48D23D6E27B2B4032EC8BDF6F884940758588E5E07B2B401A06E22B70884940E5E14E33EE7B2B404337A04A728849408621CDB3ED7B2B40A26DEB5D728849407914F8D4FB7B2B4077CE609D74884940E4DE5740FC7B2B40D6D6998C74884940CC82E4F8FC7B2B402F08F6A9748849406041F56ADB7B2B406E6F12DE79884940873DA363BD7B2B40C451031775884940B9A5D590B87B2B4053F245D6758849408FAA2688BA7B2B408E0AF72576884940BFAAC486B87B2B40C922A87576884940EAA5738FB67B2B408E0AF72576884940867071F9B47B2B40292BE16476884940CC54D2D4FC7A2B40A30B45155988494027333910ED7A2B403933B04456884940098A1F63EE7A2B400F86F01E568849403EDF60B9EF7A2B4051476C1C568849402C88DC67F07A2B40635639FE55884940014576EFF27A2B400AC9B8985588494003942B61127B2B40EF377FB850884940239AE557187B2B40FB2DF0CB4F88494052A280481A7B2B40A186CA1A50884940813A8A181B7B2B40F55613FA4F8849405C44CA051D7B2B409BAFED4850884940DFDA2ED91D7B2B40843F0D29508849402B6C06B8207B2B405445A79D508849408EC9E2FE237B2B405902DF2351884940A1206750237B2B40F4F4B63E51884940B1C79F03267B2B401EFE9AAC51884940E73CBE18257B2B401E2CADD051884940C84BEDFB267B2B409A05DA1D52884940CF989361267B2B40E2F95635528849409AC5D5123C7B2B40AAC07FA955884940EC95687F567B2B4096E7C1DD59884940DF43D323577B2B40257497C459884940BB4D1311597B2B40370D9B125A8849405A6DB4D25A7B2B40F6EFFACC59884940B130444E5F7B2B401F5503835A884940294D39155F7B2B4031C0F4AC5A88494011397D3D5F7B2B4031EE06D15A884940FFB91E3B5E7B2B4019DA4AF95A88494027E5A4E6617B2B40960F9C8E5B8849403C293861677B2B403C20BF6D5C8849409BA10271687B2B4095F5F6425C884940001FBC76697B2B40A760E86C5C8849408EBFFE356A7B2B40B96FB54E5C884940337CFABE6E7B2B4036D318085D88494057E24BF26D7B2B40B98322275D8849406D8E739B707B2B40B80D59935D884940095971056F7B2B40532E43D25D88494077589709757B2B4017B435C75E884940DB8D999F767B2B407C934B885E884940566FC3DE7A7B2B4040BD19355F8849405142FA37797B2B409A1C88765F88494042203C7F7F7B2B40EC5C6276608849404D52F41B817B2B40BD7CA136608849405C210780857B2B4028A325EA608849405DF92CCF837B2B40AB81412D61884940CC400A43897B2B40BCD28D0B6288494001DE02098A7B2B40392284EC61884940DC9F8B868C7B2B4092AF045262884940EDD632198E7B2B408C4EF11362884940BBB6B75B927B2B40E53796C162884940D23A0554937B2B40396403E96288494038482D39937B2B402D88372163884940E4011BC6937B2B40747CB438638849403E95D39E927B2B406EA5D766638849408ADEF30D967B2B40859FEEF263884940DB9C94939A7B2B40D883A4AA6488494051291B7B9B7B2B401A170E846488494057E652019C7B2B406D156987648849405199AC9B9C7B2B40A3CC06996488494044FF5FD09D7B2B40F6CA619C648849405F9099B0A27B2B40C0273163658849403DDA931BA07B2B40AE7488C965884940
http://dl-learner.org/ont/spatial#area_inside_bhf_neustadt_geometry	0103000000010000000500000001008078527B2B40F03EB579608849400200002F587B2B40842255895F88494000000045607B2B400027DFBC608849400200006A597B2B40ACDA5C866188494001008078527B2B40F03EB57960884940
\.


--
-- Data for Name: line_feature; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.line_feature (iri, the_geom) FROM stdin;
http://dl-learner.org/ont/spatial#turnerweg_geometry	010200000003000000151E34BBEE7D2B400525BB88A5884940495D7C1BAC7D2B406A40CE458D884940B17E7DBE777D2B40508479347A884940
http://dl-learner.org/ont/spatial#turnerweg_part_geometry	010200000002000000495D7C1BAC7D2B406A40CE458D884940B17E7DBE777D2B40508479347A884940
http://dl-learner.org/ont/spatial#way_inside_bhf_neustadt_geometry	01020000000200000000000022477B2B405856D2BA5F884940000000767A7B2B4050227FE767884940
\.


--
-- Data for Name: point_feature; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.point_feature (iri, the_geom) FROM stdin;
http://dl-learner.org/ont/spatial#inside_building_bhf_neustadt_geometry	010100000096218E75717B2B40AAF1D24D62884940
http://dl-learner.org/ont/spatial#outside_building_bhf_neustadt_1_geometry	0101000000FFFFFF6BC07B2B40F47C293477884940
http://dl-learner.org/ont/spatial#outside_building_bhf_neustadt_2_geometry	0101000000FFFFFF774C7A2B40A49F9AFB76884940
http://dl-learner.org/ont/spatial#on_turnerweg_geometry	0101000000495D7C1BAC7D2B406A40CE458D884940
http://dl-learner.org/ont/spatial#inside_building_bhf_neustadt_02_geometry	01010000000100C0D7587B2B401004D98760884940
\.


--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text) FROM stdin;
\.


--
-- PostgreSQL database dump complete
--
